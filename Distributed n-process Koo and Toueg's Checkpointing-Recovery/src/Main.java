//Main Class
//This is the main class for this project.
//Since we store our result to output file, this class also cleans up the files which already exists.
//This project can run on local system or in distributed environment by changing the variable online.

import java.io.File;

public class Main {

	public static void main(String[] args) throws Exception {
		Configuration config = new Configuration("config.txt");
		System.out.println(config);

		for (File file : new File(".").listFiles())
			if (file.getName().startsWith("Checkpoint_")
					|| file.getName().startsWith("Recovery_"))
				file.delete();

		boolean online = true;
		if (online) {
			new Node(Integer.parseInt(args[0]), config).go();
		} else {
			for (int i = 0; i < config.totalProcesses(); i++) {
				new Node(i, config).go();
			}
		}
	}
}
