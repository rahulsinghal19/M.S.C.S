//Node Class
//This is the node ie process which starts the application server and checkpointServer on its node.
//If this is node 0 then this will also act as a Monitor.

public class Node {

	private Application app;
	private CheckpointServer cs;
	private Configuration config;

	public Node(int id, Configuration config) {
		this.config = config;
		app = new Application(id, config);
		cs = new CheckpointServer(app, config);
	}

	public void go() {
		app.startServer();
		cs.startServer();
		if (app.id() == 0) {
			new Thread(new MonitorServer(config)).start();
		}
	}

}
