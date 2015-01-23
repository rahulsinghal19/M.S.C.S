//Checkpoint Class
//This class is used to define the components used in checkpointing algorithm. 

import java.io.Serializable;
import java.util.Arrays;

public class Checkpoint implements Serializable {

	private int[] snd, rcv, clock, fls, llr, lls;
	private int id, label;
	private int cid;

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public Checkpoint() {
	}

//This is the Constructor class
	public Checkpoint(int id, int[] snd, int[] rcv, int[] clock, int[] fls,
			int[] llr, int[] lls, int label) {
		this.snd = Arrays.copyOf(snd, snd.length);
		this.rcv = Arrays.copyOf(rcv, rcv.length);
		this.clock = Arrays.copyOf(clock, clock.length);
		this.fls = Arrays.copyOf(fls, fls.length);
		this.lls = Arrays.copyOf(lls, lls.length);
		this.llr = Arrays.copyOf(llr, llr.length);
		this.id = id;
		this.label = label;
	}
	
//Function to convert array values to string	
	@Override
	public String toString() {
		return "Checkpoint [snd=" + Arrays.toString(snd) + ", rcv="
				+ Arrays.toString(rcv) + ", clock=" + Arrays.toString(clock)
				+ ", fls=" + Arrays.toString(fls) + ", llr="
				+ Arrays.toString(llr) + ", lls=" + Arrays.toString(lls)
				+ ", id=" + id + ", label=" + label + ", cid=" + cid + "]";
	}

	public int clock(int i) {
		return clock[i];
	}

	public int snd(int i) {
		return snd[i];
	}

	public int rcv(int i) {
		return rcv[i];
	}

	public int label() {
		return label;
	}

	public int llr(int i) {
		return llr[i];
	}

	public int fls(int i) {
		return fls[i];
	}

	public int lls(int i) {
		return lls[i];
	}

	public int[] getSnd() {
		return snd;
	}

	public void setSnd(int[] snd) {
		this.snd = snd;
	}

	public int[] getRcv() {
		return rcv;
	}

	public void setRcv(int[] rcv) {
		this.rcv = rcv;
	}

	public int[] getClock() {
		return clock;
	}

	public void setClock(int[] clock) {
		this.clock = clock;
	}

	public int[] getFls() {
		return fls;
	}

	public void setFls(int[] fls) {
		this.fls = fls;
	}

	public int[] getLlr() {
		return llr;
	}

	public void setLlr(int[] llr) {
		this.llr = llr;
	}

	public int[] getLls() {
		return lls;
	}

	public void setLls(int[] lls) {
		this.lls = lls;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}

}
