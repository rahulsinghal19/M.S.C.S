import java.util.ArrayList;


public class IncrementCounter extends Thread {
	
	private static final int MAX = MutualExclusionTest.noOfOperations;
	private Object obj;

	public IncrementCounter(Bakery bakeryThread, String name) {
		this.obj = bakeryThread;
		this.setName(name);
	}
	
	public void run() {
		
		if(obj instanceof Bakery) {
			Bakery bakery = (Bakery) obj;
			for(int i=0; i<MAX; i++) {
				bakery.lock();
				bakery.unlock();
				try {
					Thread.sleep(MutualExclusionTest.interRequestDelay);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} 
	}
}
