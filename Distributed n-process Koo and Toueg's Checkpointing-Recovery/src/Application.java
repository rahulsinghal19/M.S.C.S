//Application class
//This class is where the application messages are sent from. This contains a priority queue to queue the messages.
//


import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

public class Application {

	public static final int BOTTOM = Integer.MIN_VALUE;
	private Configuration config;
	private PriorityBlockingQueue<Message> pq = new PriorityBlockingQueue<Message>();
	private int id, label;
	private int[] clock, fls, lls, llr, rcv, snd;
	private Server server;
	private TCPServerSocket ssh;
	private MessageProducer msgProducer;
	private MessageConsumer msgConsumer;
	
	
// Constructor for Application class
	public Application(int id, Configuration config) {
		this.id = id;
		this.config = config;
		this.clock = new int[config.totalProcesses()];
		this.fls = new int[config.totalProcesses()];
		this.llr = new int[config.totalProcesses()];
		this.lls = new int[config.totalProcesses()];
		this.rcv = new int[config.totalProcesses()];
		this.snd = new int[config.totalProcesses()];
		Arrays.fill(fls, BOTTOM);
		Arrays.fill(lls, BOTTOM);
		Arrays.fill(llr, BOTTOM);
		this.label = 0;
	}

	// Server thread to implement new TCP connections
	// Server for receiving application messages
	class Server implements Runnable {

		public Server() {
			ssh = new TCPServerSocket();
		}

		@Override
		public void run() {
			try {
				ssh.listen(config.port(id));
				System.out.println("Listen");
				while (true) {
					ClientSocketHandler sock = ssh.accept();
					Message msg = (Message) sock.receiveObject();
					pq.add(msg);
					sock.close();
				}

			} catch (Exception e) {
				log("AppServer Shutting down.");
				// e.printStackTrace();
			} finally {
				if (ssh != null)
					try {
						ssh.close();
					} catch (Exception e) {
						//e.printStackTrace();
					}
			}
		}

		public void stop() {
			try {
				ssh.close();
			} catch (Exception e) {
//				e.printStackTrace();
			}
		}
	}

	
// MessageConsumer thread which consumes the message it receives from other processes using Priority queue.	
	class MessageConsumer implements Runnable {

		private volatile boolean done;

		@Override
		public void run() {

			try {
				while (!done) {
					Message msg = pq.take();
					if(msg.type() == Message.Type.APP_TERMINATE) break;
					processMessage(msg);
				}
				log("Message Consumer terminating.");
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}
		}

		public void terminate() {
			done = true;
		}
	}
	
	
// This function freezes the application messages during Checkpointing or recovery
	public void freeze(boolean b) {
		msgProducer.setFreeze(b);
	}

	private void sleep(int sec) {
		try {
			Thread.sleep(sec * 1000);
		} catch (InterruptedException e) {
//			e.printStackTrace();
		}
	}

	
//This thread continuously produces application messages; sends to all its neighbours
	class MessageProducer implements Runnable {
		private volatile boolean freeze = false, done = false;
		private Random r = new Random();

		public void setFreeze(boolean b) {
			this.freeze = b;
		}

		@Override
		public void run() {
			while (!done) {
				sleep(r.nextInt(5) + 3);
				if (!freeze) {
					synchronized (clock) {
						clock[id]++;
						try {
							for (int j : config.neighbours(id)) {
								sendMessage(new Message(id, j, label++,
										Message.Type.APP_COUNT, clock));
							}
						} catch (Exception e) {
						}
					}
				}
			}
			log("Message Producer terminating.");
		}

		public void terminate() {
			done = true;
		}
	}

	
// This is used to start the threads of this class
	public void startServer() {
		new Thread(this.msgConsumer = new MessageConsumer()).start();
		new Thread(this.server = new Server()).start();
		new Thread(this.msgProducer = new MessageProducer()).start();
	}

// This function is used to send message to the Cohorts depending on the LLS and FLS values	
	public void sendMessage(Message out) {
		ClientSocketHandler client;
		try {
			if (fls[out.to()] == BOTTOM)
				fls[out.to()] = out.label();
			lls[out.from()] = out.label();
			client = ssh.makeSocket(config.host(out.to()),
					config.port(out.to()));
			client.sendObject(out);
			client.close();
			snd[out.to()]++;
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}

	
// this function is used by the MessageConsumer class to process the incoming application messages
	public void processMessage(Message msg) {
		log("Received %s", msg);
		synchronized (clock) {
			rcv[msg.from()]++;
			switch (msg.type()) {
			case APP_COUNT:
				// log("My Clock : %s", Arrays.toString(clock));
				int[] mClock = (int[]) msg.getPayload();
				// log("Message Clock from %d : %s", msg.from(),
				// Arrays.toString(mClock));
				for (int i = 0; i < clock.length; i++)
					clock[i] = Math.max(clock[i], mClock[i]);
				clock[id]++;
				// log("My Updated Clock : %s", Arrays.toString(clock));
				//
				llr[msg.from()] = msg.label();
				break;
			default:
				break;
			}
		}
	}

	public void stopServer() {
		server.stop();
	}

// This function is used to get the fls,llr,lls,send,receive and clock values for a specific checkpoint.	
	public void load(Checkpoint checkpoint) {
		synchronized (clock) {
			//
			for (int i = 0; i < config.totalProcesses(); i++) {
				clock[i] = checkpoint.clock(i);
				snd[i] = checkpoint.snd(i);
				rcv[i] = checkpoint.rcv(i);
				fls[i] = checkpoint.fls(i);
				lls[i] = checkpoint.lls(i);
				llr[i] = BOTTOM;
			}
			this.label = checkpoint.label();
		}
	}

//This is used to get the checkpoint state and to update the vector clock for this particular state.	
	public synchronized Checkpoint state() {
		synchronized (clock) {
			clock[id]++;
			return new Checkpoint(id, snd, rcv, clock, fls, llr, lls, label);
		}
	}

	private void log(String m, Object... args) {
		System.out.println(String.format("[%d] - %s", id,
				String.format(m, args)));
	}

	public int id() {
		return id;
	}

	public int llr(int i) {
		return llr[i];
	}

	public int lls(int i) {
		return lls[i];
	}

	public int fls(int i) {
		return fls[i];
	}

	public void reset_fls_llr(int j) {
		llr[j] = BOTTOM;
		fls[j] = BOTTOM;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("LLS : ").append(Arrays.toString(lls)).append('\n');
		sb.append("FLS : ").append(Arrays.toString(fls)).append('\n');
		sb.append("LLR : ").append(Arrays.toString(llr)).append('\n');

		return super.toString();
	}

	public void shutdown() {
		pq.add(new Message(0, 0, Message.Type.APP_TERMINATE, null));
		msgProducer.terminate();
		msgConsumer.terminate();
		server.stop();
	}
}
