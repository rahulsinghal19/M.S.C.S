//Configuration class
//This class is used to read configuration file.

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Configuration {
	private String fileName;
	private int N;
	private String[] hosts;
	private int[] ports;
	private int[][] neighbor;
	private String path;
	private int minDelay;

	public Configuration(String fileName) throws Exception {
		this.fileName = fileName;
		readConfiguration();
	}

	private void readConfiguration() {
		try {
			Scanner scan = new Scanner(new File(fileName));
			N = Integer.parseInt(scan.nextLine());
			hosts = new String[N];
			ports = new int[N];
			neighbor = new int[N][];
			for (int i = 0; i < N; i++) {
				hosts[i] = scan.next();
				ports[i] = scan.nextInt();
				String[] adj = scan.next().split(",");
				neighbor[i] = new int[adj.length];
				for (int j = 0; j < adj.length; j++)
					neighbor[i][j] = Integer.parseInt(adj[j]);
				scan.nextLine();
			}
			path = scan.nextLine().toLowerCase();
			minDelay = Integer.parseInt(scan.nextLine());
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public String host(int id) {
		return hosts[id];
	}

	public int port(int id) {
		return ports[id];
	}

	public int totalProcesses() {
		return N;
	}
	
	public int getMinDelay() {
		return minDelay;
	}

	@Override
	public String toString() {
		return "Configuration [fileName=" + fileName + ", N=" + N + ", hosts="
				+ Arrays.toString(hosts) + ", ports=" + Arrays.toString(ports)
				+ ", neighbor=" + Arrays.deepToString(neighbor) + ", path="
				+ path + "]";
	}

	public String getPath() {
		return path;
	}

	public int[] neighbours(int id) {
		return neighbor[id];
	}

	public int getMonitorPort() {
		return 8888;
	}

}
