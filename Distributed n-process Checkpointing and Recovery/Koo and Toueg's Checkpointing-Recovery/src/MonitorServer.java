//MonnitorServer Class
//This class is used for testing the consistency of the checkpoints after the rollback. This class verifies 
//whether the system is consistent or not.
//This class is also used for termination.

import java.util.HashMap;
import java.util.Map;

public class MonitorServer implements Runnable {
	private ServerSocketHandler ssh = new TCPServerSocket();
	private Configuration config;
	private Map<Integer, Checkpoint[]> cps = new HashMap<Integer, Checkpoint[]>();
	private int[] cpCount;
	private int lastCP;

	public MonitorServer(Configuration config) {
		this.config = config;
		this.cpCount = new int[config.totalProcesses()];
	}

	@Override
	public void run() {
		try {
			ssh.listen(config.getMonitorPort());

			boolean terminateReceived = false, done = false;
			while (!done) {
				ClientSocketHandler client = ssh.accept();
				Message msg = (Message) client.receiveObject();

				switch (msg.type()) {
				case MONITOR_RECORD_CHECKPOINT:
					Checkpoint cp = (Checkpoint) msg.getPayload();
					Checkpoint[] l = cps.get(msg.label());
					lastCP = msg.label();
					if (l == null)
						cps.put(msg.label(),
								l = new Checkpoint[config.totalProcesses()]);
					l[msg.from()] = cp;
					cpCount[msg.label()]++;
					// when I have received all the checkpoints, I perform validation on the checkpoints to determine the consistency of the state / checkpoints of the system
					if (cpCount[msg.label()] == config.totalProcesses()) {
						log("%sReceived all checkpoints for %d",
								AnsiColorConstants.ANSI_YELLOW, msg.label());
						for (Checkpoint c : l)
							log(c.toString());
						boolean valid = new Verifier(l).isValid();
						log("%sCheckpoint Consistent : %s",
								valid ? AnsiColorConstants.ANSI_GREEN
										: AnsiColorConstants.ANSI_RED, valid);

						if (terminateReceived
								&& cpCount[lastCP] == config.totalProcesses()) {
							sendTerminate();
							done = true;
						}
					}
					break;
				case MONITOR_TERMINATE:
					log("Received MONITOR_TERMINATE from %d.", msg.from());
					terminateReceived = true;

					if (cpCount[lastCP] == config.totalProcesses()) {
						sendTerminate();
						done = true;
					}

				default:
					break;
				}
				client.close();
			}
			ssh.close();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	private void sendTerminate() {
		for (int i = config.totalProcesses() - 1; i >= 0; i--)
			sendMessage(new Message(-1, i, Message.Type.TERMINATE, null));

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

	private void log(String m, Object... args) {
		System.out.println(String.format("[Monitor] - %s%s",
				String.format(m, args), AnsiColorConstants.ANSI_RESET));
	}

}
