import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

//This is the class which implements the logic of Maekawa's algorithm. All the messages like REQUEST, GRANT, YIELD, RELEASE, INQUIRE and FAILED are used 
//in this class. We have used the concept of Lamport's Logical Clock to increment the timestamp.
//The process sends the message to Monitor server to enter CS. It waits for ACK from Monitor after leaving CS to release the quorum.

public class Node {

	private final int id;
	private final int serverPort;
	private final int[] quorumIDS;
	private final ServerSocketHandler ssh;
	private PriorityBlockingQueue<Message> pq = new PriorityBlockingQueue<>();
	private AtomicInteger timestamp = new AtomicInteger();
	private Mutex mutex = new MaekawaMutex();
	private ReentrantLock lock = new ReentrantLock();
	private Condition lockCondition;
	private boolean inCS = false;
	private Quorum quorum = new Quorum();
	private volatile BitSet grant = new BitSet(1024);
	private volatile BitSet failed = new BitSet(1024);
	private Configuration config;
	private CriticalSection cs;
	private volatile boolean waitForACK;
	
	//Used to get the Timestamp by incrementing it by 1 everytime it is called.
	public synchronized int nextTimestamp(int ts) {
		timestamp.set(Math.max(timestamp.get(), ts) + 1);
		return timestamp.get();
	}

	public synchronized int nextTimestamp() {
		return timestamp.incrementAndGet();
	}

	class MaekawaMutex implements Mutex {

		//this function is called when a process wants to enter the Critical section
		@Override
		public void csEnter() {

			log("csEnter");
			log("Sending to my quorum : %s", Arrays.toString(quorumIDS));
			int ts = nextTimestamp();
			Message monitorMessage = new Message(id, config.quorumId(id), ts,
					Message.Type.MONITOR_ENTERCS);
			for (int qid : quorumIDS) {
				sendMessage(new Message(id, qid, ts, Message.Type.Q_REQUEST));
			}
			while (grant.cardinality() < quorumIDS.length)
				;
			waitForACK = true;
			sendMessageToMonitor(new Message(id, 0, -1,
					Message.Type.MONITOR_ENTERCS, monitorMessage));
			log("Waiting for EnterCS ACK");
			while (waitForACK)
				;
		}

		//this function is called when a process wants to leave the Critical section
		@Override
		public void csLeave() {
			log("csLeave");
			int ts = nextTimestamp();
			waitForACK = true;
			sendMessageToMonitor(new Message(id, 0, -1,
					Message.Type.MONITOR_LEAVECS));
			log("Waiting for LeaveCS ACK");
			while (waitForACK)
				;
			for (int qid : quorumIDS) {
				sendMessage(new Message(id, qid, ts, Message.Type.Q_RELEASE));
			}
			grant.clear();
			failed.clear();
		}

	}
	
	// this is a server thread for the node

	class Server implements Runnable {

		@Override
		public void run() {
			try {
				ssh.listen(serverPort);
				log("Server up!");
				while (true) {
					ClientSocketHandler client = ssh.accept();
					Message msg = (Message) client.receiveObject();
					pq.add(msg);
					client.close();
					if (msg.getType() == Message.Type.TERMINATE)
						break;
				}
				ssh.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
	//this thread receives the incoming message and sends it to processMessage for processing.

	class MessageConsumer implements Runnable {

		@Override
		public void run() {

			while (true) {
				try {
					Message m = pq.take();
					if (m.getType() == Message.Type.TERMINATE)
						break;
					processMessage(m);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			log2("Shutting Down!");
		}
	}

	
	//Function which processes the messages received by the node based on the type of the message.
	private synchronized void processMessage(Message msg) {
		synchronized (quorum) {

			nextTimestamp(msg.getTimestamp());
			log("Received message : %s", msg);
			switch (msg.getType()) {
			// MONITOR Messages
			case MONITOR_LEAVECS_ACK:
			case MONITOR_ENTERCS_ACK:
				waitForACK = false;
				break;
			// Quorum related messages
			case Q_REQUEST:
				if (quorum.isLocked()) {
					log("Quorum is locked. Comparing %s and %s", msg,
							quorum.getLastRequest());
					if (msg.compareTo(quorum.getLastRequest()) < 0) {
						// Send INQUIRE
						quorum.add(msg);
						sendMessage(new Message(id, quorum.getLastRequest()
								.from(), nextTimestamp(),
								Message.Type.Q_INQUIRE));

					} else {
						// Enqueue to Quorum
						quorum.add(msg);
						// send failed to msg.from()
						sendMessage(new Message(id, msg.from(),
								nextTimestamp(), Message.Type.Q_FAILED));
					}

				} else {
					quorum.setLastRequest(msg);
					sendMessage(new Message(id, msg.from(), nextTimestamp(),
							Message.Type.Q_GRANT));
				}

				break;

			case Q_RELEASE:
				if (quorum.hasPending()) {
					Message m = quorum.pop();
					quorum.setLastRequest(m);
					sendMessage(new Message(id, m.from(), nextTimestamp(),
							Message.Type.Q_GRANT));
				} else {
					quorum.setLastRequest(null);
				}
				break;
			case Q_YIELD:
				Message m = quorum.pop();
				if (m != null) {
					quorum.add(quorum.getLastRequest());
					quorum.setLastRequest(m);
					sendMessage(new Message(id, m.from(), nextTimestamp(),
							Message.Type.Q_GRANT));
				}
				break;

			// Process related messages
			case Q_GRANT:
				grant.set(msg.from());
				log("grant : %s", grant);
				break;
			case Q_INQUIRE:
				if (!inCS) {
					grant.clear(msg.from());
					failed.set(msg.from());
					sendMessage(new Message(id, msg.from(), nextTimestamp(),
							Message.Type.Q_YIELD));

				}
				break;
			case Q_FAILED:
				failed.set(msg.from());
				break;
			default:
				break;
			}
		}
	}
	
	//Function to send message to another process ie quorum.

	private void sendMessage(Message out) {
		try {
			log("Sending Message : %s", out);
			ClientSocketHandler client = ssh.makeSocket(config.host(out.to()),
					config.port(out.to()));
			client.sendObject(out);
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Function to send message to the monitor server by the process.

	private void sendMessageToMonitor(Message out) {
		try {
			log("Sending Monitor Message : %s", out);
			ClientSocketHandler client = ssh.makeSocket(config.host(0),
					config.getMonitorPort());
			client.sendObject(out);
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Function to create the thread for each process Node.
	public Node(Configuration config, int id, int serverPort, int[] quorumIDS,
			CriticalSection cs) {
		super();
		this.id = id;
		this.serverPort = serverPort;
		this.quorumIDS = quorumIDS;
		this.lockCondition = lock.newCondition();
		this.ssh = new TCPServerSocket();
		this.cs = cs;
		this.config = config;
		new Thread(new Server()).start();
		new Thread(new MessageConsumer()).start();
		if (id == 0)
			new Thread(new MonitorServer(config)).start();
	}

	private void sleep(int sec) {
		try {
			Thread.sleep(sec * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//Function to execute the CS

	public void go() {
		Random r = new Random();
		int c = 0;
		while (c < 10) {
			//sleep(id == 0 ? 4 : 5);
			mutex.csEnter();
			inCS = true;
			cs.execute();
			log("done executing cs %d times!", c);
			mutex.csLeave();
			inCS = false;
			c++;
		}
		sendMessageToMonitor(new Message(id, 0, -1, Message.Type.DONE));

	}

	
	//Function to print the output to the log
	
	private void log(String m, Object... args) {
		if (false) {
			System.out.println(String.format("[%s - %d] - %s", id,
					timestamp.get(), String.format(m, args)));
		}
	}

	private void log2(String m, Object... args) {
		System.out.println(String.format("[%s - %d] - %s", id, timestamp.get(),
				String.format(m, args)));
	}

}
