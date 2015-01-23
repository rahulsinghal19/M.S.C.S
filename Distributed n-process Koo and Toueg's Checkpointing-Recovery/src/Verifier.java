//Verifier Class
//This class is used to verify if all the files have been created after each event(checkpoint / recovery)
//This validates the files created using xml.

import java.beans.XMLDecoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Verifier {

	Checkpoint[] cps;

	public Verifier(String prefix) {
		List<Checkpoint> list = new ArrayList<>();
		for (File file : new File(".").listFiles())
			if (file.getName().startsWith(prefix)
					&& file.getName().endsWith(".xml"))
				list.add(readXMLFile(file));
		cps = list.toArray(new Checkpoint[list.size()]);
	}

	public Verifier(Checkpoint[] cps) {
		this.cps = cps;
	}

	// Verifies the pair wise concurrent checkpoints in O(1); compares the ith and jth index of the process i & j
	public boolean isValid() {
		for (int i = 0; i < cps.length; i++)
			for (int j = i + 1; j < cps.length; j++) {

				if (!(cps[i].clock(i) > cps[j].clock(i) && cps[i].clock(j) < cps[j]
						.clock(j)))
					return false;
			}
		return true;
	}

	private Checkpoint readXMLFile(File file) {
		Checkpoint cp = null;
		XMLDecoder decoder;
		try {
			decoder = new XMLDecoder(new FileInputStream(file));
			cp = (Checkpoint) decoder.readObject();
			decoder.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return cp;
	}

	//manual testing purpose
	public static void main(String[] args) {
		// System.out.println(new Verifier("Checkpoint_0").isValid());
		// System.out.println(new Verifier("Checkpoint_1").isValid());
		// System.out.println(new Verifier("Checkpoint_2").isValid());
		System.out.println(new Verifier("Recovery_0").isValid());
		System.out.println(new Verifier("Recovery_1").isValid());
		System.out.println(new Verifier("Recovery_2").isValid());
		System.out.println(new Verifier("Recovery_3").isValid());
	}

}
