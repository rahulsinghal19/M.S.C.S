//This is the Main Class. Here we have two methods to implement this algorithm one on a single local system and other online using
//distributed systems. We read the config file and create a thread for the process in NodeRunner class.

public class Main {
	public final static boolean online = true;

	public static void main(String[] args) throws Exception {
		if (online)
			new Main().go(Integer.parseInt(args[0]));
		else
			new Main().go(-1);
	}

	//two ways to run this code. one on local machine and other online on UTdallas linux machines.
	private void go(int id) throws Exception {
		Configuration config = new Configuration("configuration.txt");
		System.out.println(config);

		if (online) {
			new Node(config, id, config.port(id), config.quorum(id),
					new DummyCriticalSection(id)).go();
		} else {
			for (int i = 0; i < config.totalProcesses(); i++) {
				Node node = new Node(config, i, config.port(i),
						config.quorum(i), new DummyCriticalSection(i));
				new Thread(new NodeRunner(node)).start();
			}
		}
	}

	
	//The thread which runs the node
	class NodeRunner implements Runnable {

		private Node node;

		public NodeRunner(Node node) {
			super();
			this.node = node;
		}

		@Override
		public void run() {
			node.go();
		}

	}

}
