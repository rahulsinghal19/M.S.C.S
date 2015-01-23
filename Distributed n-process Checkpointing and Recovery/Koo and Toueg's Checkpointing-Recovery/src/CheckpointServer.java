//CheckpointServer Class
//This class is used to initiate a Checkpoint event and Recovery event of Koo and Toueg Protocol

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

public class CheckpointServer {

	private Application app;
	private volatile Checkpoint perm, temp;
	private volatile boolean hasToken = false, commitedToRecovery;
	private ServerSocketHandler ssh;
	private Configuration config;
	private String path;
	private BitSet cohorts;
	private Set<Integer> parents;
	private PriorityBlockingQueue<Message> pq = new PriorityBlockingQueue<Message>();
	
	
// Constructor for Class
	public CheckpointServer(Application app, Configuration config) {
		this.app = app;
		this.perm = app.state();
		this.config = config;
		this.path = config.getPath();
		this.hasToken = app.id() == head();
		this.cohorts = new BitSet(config.totalProcesses());
		this.parents = new HashSet<>();
		log("hasToken : %s", hasToken);
	}

	private void sleep(int sec) {
		try {
			Thread.sleep(sec * 1000);
		} catch (InterruptedException e) {
			// e.printStackTrace();
		}
	}

//MessageConsumer thread consumes the control messages broadcasted during CP event or Recovery event 
	class MessageConsumer implements Runnable {

		@Override
		public void run() {

			while (true) {
				try {
					Message msg = pq.take();
					if (msg.type() == Message.Type.APP_TERMINATE)
						break;
					processMessage(msg);
				} catch (InterruptedException e) {
					// e.printStackTrace();
				}
			}
		}
	}
	
	
//CPserver thread is used to create TCP connections and add the message in Priority queue.
//Receives the checkpointing / recovery messages from other processes
	class CPServer implements Runnable {

		@Override
		public void run() {
			ssh = new TCPServerSocket();
			try {
				ssh.listen(config.port(app.id()) + 1000);
				while (true) {
					ClientSocketHandler client = ssh.accept();
					Message msg = (Message) client.receiveObject();
					pq.add(msg);
					client.close();
				}
			} catch (Exception e) {
				log("CPServer Shutting down");
				// e.printStackTrace();
			}
		}
	}
	
	
//This thread is used to check system is performing Checkpoint/Recovery.
	class Checker implements Runnable {

		@Override
		public void run() {
			sleep(config.getMinDelay());
			if (hasToken) {
				if (head() != app.id()) {
					passToken();
				} else {
					log("Gonna initiate checkpoint/recovery");

					cohorts.clear();
					// a) freeze sending
					app.freeze(true);
					log("Freezing Application");
					if (isCheckpoint()) {
						int cid = cid();
						if (cid < 0)
							return;
						log("Performing Checkpoint #%d procedure ", cid);
						// b) initiate checkpoint phase 1, take tentative and
						// send
						// request to neighbors
						temp = app.state();
						temp.setCid(cid);
						log(temp.toString());
						for (int j : config.neighbours(app.id()))
							if (app.llr(j) != Application.BOTTOM) {
								sendMessage(new Message(app.id(), j, cid,
										Message.Type.REQUEST_CHECKPOINT,
										new Integer(app.llr(j))));
								app.reset_fls_llr(j);
								cohorts.set(j);
							}
						if (cohorts.cardinality() == 0) {
							perm = temp;
							temp = null;
						}
					} else { // perform recovery
						log("Performing Recovery procedure");
						cohorts.clear();
						int rid = cid();
						if (rid < 0)
							return;
						for (int j : config.neighbours(app.id())) {
							sendMessage(new Message(app.id(), j, rid,
									Message.Type.REQUEST_RECOVERY, new Integer(
											app.lls(j))));
							cohorts.set(j);
						}
						toFile(String.format("Recovery_%d_%d.txt", rid,
								app.id()), perm, rid);
						app.load(perm);
					}
				}
				// c) wait for response
				// d) if yes from all, take checkpoint
				// else discard tentative
				// e) pass token to next in line
			}

		}
	}

	//returns the information of the path at the beginning of the path string from the token
	private int head() {
		if (path.length() == 0)
			return -1;
		return Integer.parseInt(path.split(";")[0].split(",")[0]);
	}

	//parses the token to retrieve the type of operation to be performed
	private int cid() {
		if (path.length() == 0)
			return -1;
		return Integer.parseInt(path.split(";")[0].split(",")[2]);
	}

	private boolean isCheckpoint() {
		return path.split(";")[0].split(",")[1].equalsIgnoreCase("C");
	}

	//Method to send the token to the next process from the path string / token
	public void passToken() {
		if (head() == app.id()) { // I'm supposed to have the token
			path = path.substring(path.indexOf(';') + 1);
		}
		if (path.length() == 0) {
			sendTerminate();
		} else if (head() != app.id()) { // if token belongs to someone else
			sendMessage(new Message(app.id(), head(), Message.Type.TOKEN, path));
			hasToken = false;
		}
	}

	private void sendTerminate() {
		sendMessageToMonitor(new Message(app.id(), -1,
				Message.Type.MONITOR_TERMINATE, null));
	}

	public void sendMessage(Message out) {
		log("Sending message %s", out);
		ClientSocketHandler client;
		try {
			client = ssh.makeSocket(config.host(out.to()),
					config.port(out.to()) + 1000);
			client.sendObject(out);
			client.close();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

//This is used to start the threads in this class	
	public void startServer() {
		new Thread(new CPServer()).start();
		if (hasToken)
			new Thread(new Checker()).start();
		new Thread(new MessageConsumer()).start();
	}

//This is used to write the output of each event to a file .	
	private void toFile(String fileName, Checkpoint contents, int cid) {
		try {
			log("Creating file %s", fileName);
			PrintWriter out = new PrintWriter(new FileOutputStream(new File(
					fileName), true));
			out.println(contents.toString());
			out.println();
			out.close();
			//
			// XMLEncoder encoder = new XMLEncoder(new FileOutputStream(new
			// File(
			// fileName + ".xml")));
			// encoder.writeObject(contents);
			// encoder.close();

			sendMessageToMonitor(new Message(app.id(), -1, cid,
					Message.Type.MONITOR_RECORD_CHECKPOINT, contents));

		} catch (FileNotFoundException e) {
			// e.printStackTrace();
		}
	}

//This function is called by the MessageConsumer class to process the incoming message based on the message type.	
	public void processMessage(Message msg) {
		log("Received message : %s", msg);
		switch (msg.type()) {
		case TERMINATE:
			System.exit(0);
			break;
		case TOKEN:
			path = (String) msg.getPayload();
			hasToken = true;
			log("Path updated to %s", path);
			new Thread(new Checker()).start();
			break;
		case REQUEST_RECOVERY:
			parents.add(msg.from());
			Integer lls = (Integer) msg.getPayload();
			if (!commitedToRecovery) {
				if (app.llr(msg.from()) > lls) { // condition to take a recovery based on the vector received
					commitedToRecovery = true;
					cohorts.clear();
					for (int j : config.neighbours(app.id())) {
						if (j != msg.from()) {
							sendMessage(new Message(app.id(), j, msg.label(),
									Message.Type.REQUEST_RECOVERY, new Integer(
											app.lls(j))));
							cohorts.set(j);
						}
					}
					if (cohorts.cardinality() == 0) {
						parents.remove(msg.from());
						sendMessage(new Message(app.id(), msg.from(),
								Message.Type.ACK_RECOVERY, ""));
					}

				} else {
					commitedToRecovery = true;
					toFile(String.format("Recovery_%d_%d.txt", msg.label(),
							app.id()), perm, msg.label());
					sendMessage(new Message(app.id(), msg.from(),
							Message.Type.ACK_RECOVERY, ""));
				}

			} else { //if I do not have to perform a recovery, I send an ack message to the parent
				sendMessage(new Message(app.id(), msg.from(),
						Message.Type.ACK_RECOVERY, ""));

			}

			break;
		case REQUEST_CHECKPOINT://
			if (temp == null) {
				int currentCheckpointID = msg.label();
				Integer pllr = (Integer) msg.getPayload();
				// check condition
				if (pllr >= app.fls(msg.from())
						&& app.fls(msg.from()) > Application.BOTTOM) {
					log("Taking Temp Checkpoint because of %d", msg.from());
					parents.add(msg.from());

					log("Freezing Application");
					// take a checkpoint
					app.freeze(true);
					temp = app.state();
					temp.setCid(currentCheckpointID);
					cohorts.clear();
					for (int j : config.neighbours(app.id()))
						if (j != msg.from() && app.llr(j) != Application.BOTTOM) { // condition checking before sending checkpoint requests to my cohorts
							log("sending to %d", j);
							sendMessage(new Message(app.id(), j,
									currentCheckpointID,
									Message.Type.REQUEST_CHECKPOINT,
									new Integer(app.llr(j))));
							app.reset_fls_llr(j);
							cohorts.set(j);
						}
					log("Cohorts : %s", cohorts);
					if (cohorts.cardinality() == 0) {
						parents.remove(msg.from());
						sendMessage(new Message(app.id(), msg.from(),
								Message.Type.ACK_CHECKPOINT, ""));
					}

				} else {
					toFile(String.format("Checkpoint_%d_%d.txt",
							currentCheckpointID, app.id()), perm,
							currentCheckpointID);
					sendMessage(new Message(app.id(), msg.from(),
							Message.Type.ACK_CHECKPOINT, ""));
				}
			} else { //if I have already taken a checkpoint, (this is the second request received; so send ack msg)
				sendMessage(new Message(app.id(), msg.from(),
						Message.Type.ACK_CHECKPOINT, ""));

			}
			break;
		case TAKE_CHECKPOINT:
			if (temp != null) {
				perm = temp;
				temp = null;
				toFile(String.format("Checkpoint_%d_%d.txt", perm.getCid(),
						app.id()), perm, perm.getCid());
				app.freeze(false);
				for (int j : config.neighbours(app.id()))
					sendMessage(new Message(app.id(), j,
							Message.Type.TAKE_CHECKPOINT, ""));
			}
			break;
		case ACK_CHECKPOINT:
			cohorts.clear(msg.from());
			if (cohorts.cardinality() == 0) {
				if (!hasToken) {
					for (int parent : parents)
						sendMessage(new Message(app.id(), parent,
								Message.Type.ACK_CHECKPOINT, ""));
					parents.clear();
				} else { // initiator
					for (int j : config.neighbours(app.id()))
						sendMessage(new Message(app.id(), j,
								Message.Type.TAKE_CHECKPOINT, ""));

					perm = temp;
					temp = null;
					toFile(String.format("Checkpoint_%d_%d.txt", perm.getCid(),
							app.id()), perm, perm.getCid());
					sleep(config.getMinDelay());
					passToken();
					app.freeze(false);
				}
			}

			break;

		case ROLLBACK:
			if (commitedToRecovery) {
				toFile(String.format("Recovery_%d_%d.txt", msg.label(),
						app.id()), perm, msg.label());
				commitedToRecovery = false;
				for (int j : config.neighbours(app.id()))
					sendMessage(new Message(app.id(), j, msg.label(),
							Message.Type.ROLLBACK, ""));

				app.load(perm);
				app.freeze(false);
			}

			break;

		case ACK_RECOVERY:
			cohorts.clear(msg.from());
			log("Cohorts : %s", cohorts);
			if (cohorts.cardinality() == 0) {

				if (!hasToken) {
					for (int parent : parents)
						sendMessage(new Message(app.id(), parent,
								Message.Type.ACK_RECOVERY, ""));
					parents.clear();
				} else { // initiator
							// --
					for (int j : config.neighbours(app.id())) {
						sendMessage(new Message(app.id(), j, cid(),
								Message.Type.ROLLBACK, ""));
					}
					commitedToRecovery = false;
					app.freeze(false);
					passToken();
				}
			}
			break;
		case NACK_CHECKPOINT:
			break;
		default:
			break;
		}

	}

	
//This is used to send Messages to the Monitor class which helps during testing to determine if the CP is consistent or not.	
	private void sendMessageToMonitor(Message out) {
		try {
			log("Sending Monitor Message : %s", out);
			ClientSocketHandler client = ssh.makeSocket(config.host(0),
					config.getMonitorPort());
			client.sendObject(out);
			client.close();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	private void log(String m, Object... args) {
		System.out.println(String.format("[CheckpointServer %d] - %s",
				app.id(), String.format(m, args)));
	}
}
