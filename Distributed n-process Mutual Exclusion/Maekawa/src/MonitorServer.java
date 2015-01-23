import java.util.BitSet;

//This is the auxiliary process thread used to monitor the processes entering CS for the sake of Testing.
//Monitor also terminates all the process after all are done.

public class MonitorServer implements Runnable {
	private ServerSocketHandler ssh = new TCPServerSocket();
	private Message servingRequest = null;
	private int[] csCount;
	private Configuration config;
	private BitSet done;

	public MonitorServer(Configuration config) {
		this.config = config;
		this.csCount = new int[this.config.totalProcesses()];
		this.done = new BitSet(this.config.totalProcesses());
	}

	@Override
	public void run() {
		try {
			ssh.listen(config.getMonitorPort());

			boolean terminateSent = false;
			while (!terminateSent) {
				ClientSocketHandler client = ssh.accept();
				Message msg = (Message) client.receiveObject();

				switch (msg.getType()) {
				case MONITOR_ENTERCS:
					if (servingRequest == null) {
						servingRequest = (Message) msg.getPayload();
						int from = servingRequest.from();
						csCount[from]++;
						log("%s%d is entering CS for the %d time with timestamp %d!",
								AnsiColorConstants.ANSI_YELLOW, from,
								csCount[from], servingRequest.getTimestamp());
						
						sendMessage(new Message(-1, from, -1, Message.Type.MONITOR_ENTERCS_ACK));

					} else {
						Message request = (Message) msg.getPayload();
						log("%sBoth %d and %d are in CS!",
								AnsiColorConstants.ANSI_RED,
								servingRequest.from(), request.from());
					}
					break;
				case MONITOR_LEAVECS:
					if (servingRequest != null) {
						if (msg.from() == servingRequest.from()) {
							
							log("%s%d is leaving CS!",
									AnsiColorConstants.ANSI_CYAN,
									servingRequest.from());
							servingRequest = null;
							sendMessage(new Message(-1, msg.from(), -1, Message.Type.MONITOR_LEAVECS_ACK));

						} else {
							log("%s%d is leaving CS, but %d is currently in CS.",
									AnsiColorConstants.ANSI_RED, msg.from(),
									servingRequest.from());
						}

					} else {
						log("%s%d is leaving CS but no record of entering CS!!!",
								AnsiColorConstants.ANSI_RED, msg.from());
					}
					break;
				case DONE:
					done.set(msg.from());
					log("%sReceived DONE from %d",
							AnsiColorConstants.ANSI_WHITE, msg.from());
					if (done.cardinality() == config.totalProcesses()) {
						log("%sReceived all DONE messages, sending TERMINATE to all.",
								AnsiColorConstants.ANSI_WHITE);
						for (int i = 0; i < config.totalProcesses(); i++) {
							sendMessage(new Message(-1, i, Integer.MAX_VALUE,
									Message.Type.TERMINATE));
						}
						terminateSent = true;
					}
					break;
				default:
					break;
				}

			}
			ssh.close();
			log("%sMonitor terminating!", AnsiColorConstants.ANSI_WHITE);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void sendMessage(Message out) {
		try {
			ClientSocketHandler client = ssh.makeSocket(config.host(out.to()),
					config.port(out.to()));
			client.sendObject(out);
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void log(String m, Object... args) {
		System.out.println(String.format("[Monitor - %d] - %s%s",
				System.currentTimeMillis(), String.format(m, args),
				AnsiColorConstants.ANSI_RESET));
	}

}
