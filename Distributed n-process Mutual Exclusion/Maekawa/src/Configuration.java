import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

//This class reads from the Configuration file and also validates the input like total number of processes, quorum size and quorum members.

public class Configuration {
	private String fileName;
	private int[][] quorums;
	private int N;
	private String[] hosts;
	private int[] ports;
	private int[] qids;
	private int Q;
	private int monitorPort;

	public Configuration(String fileName) throws Exception {
		this.fileName = fileName;
		readConfiguration();

		if (false && !validateQuorum())
			throw new Exception("Invalid quorum found!");
	}

	private void readConfiguration() {
		try {
			Scanner scan = new Scanner(new File(fileName));
			N = Integer.parseInt(scan.nextLine());
			monitorPort = Integer.parseInt(scan.nextLine());
			hosts = new String[N];
			ports = new int[N];
			qids = new int[N];
			for (int i = 0; i < N; i++) {
				hosts[i] = scan.next();
				ports[i] = scan.nextInt();
				qids[i] = scan.nextInt();
				scan.nextLine();
			}
			Q = Integer.parseInt(scan.nextLine());
			quorums = new int[Q][];
			for (int i = 0; i < Q; i++) {
				int size = scan.nextInt();
				quorums[i] = new int[size];
				for (int j = 0; j < size; j++)
					quorums[i][j] = scan.nextInt();
			}
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public int getMonitorPort() {
		return monitorPort;
	}

	public String host(int id) {
		return hosts[id];
	}

	public int port(int id) {
		return ports[id];
	}

	public int quorumId(int id) {
		return qids[id];
	}

	public int[] quorum(int id) {
		return quorums[id];
	}

	public boolean validateQuorum() {
		for (int i = 0; i < quorums.length; i++) {
			Set<Integer> iset = new HashSet<>();
			for (int q : quorums[i]) {
				if (q < 0 || q >= N)
					return false;
				iset.add(q);
			}
			for (int j = i + 1; j < quorums.length; j++) {
				Set<Integer> jset = new HashSet<>();
				for (int q : quorums[j]) {
					if (q < 0 || q >= N)
						return false;
					jset.add(q);
				}
				Set<Integer> intersection = new HashSet<Integer>(iset);
				intersection.retainAll(jset);
				if (intersection.size() == 0)
					return false;
			}
		}
		for (int id = 0; id < totalProcesses(); id++) {
			boolean found = false;
			int[] quorum = quorums[id];
			for (int i = 0; i < quorum.length && !found; i++)
				if (quorum[i] == id)
					found = true;
			if(!found) return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Configuration [fileName=" + fileName + ", quorums="
				+ Arrays.deepToString(quorums) + ", N=" + N + ", hosts="
				+ Arrays.toString(hosts) + ", ports=" + Arrays.toString(ports)
				+ ", qids=" + Arrays.toString(qids) + ", Q=" + Q + "]";
	}

	public int totalProcesses() {
		return N;
	}

}
