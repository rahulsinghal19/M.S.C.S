import java.util.ArrayList;


public class MutualExclusionTest {
	
	static int interRequestDelay = -1;
	static int noOfOperations = -1;
	static int noOfThreads= -1;

	public static void main(String[] args) {
		noOfThreads = Integer.parseInt(args[0]);
		interRequestDelay = Integer.parseInt(args[1]);
		noOfOperations = Integer.parseInt(args[2]);
		
		Bakery bakery = new Bakery(noOfThreads);
		Thread[] threads = new Thread[noOfThreads];
		for(int i=0; i<noOfThreads; i++) {
			threads[i] = new IncrementCounter(bakery, String.valueOf(i));
		}
		long start = System.currentTimeMillis();
		for(int i=0; i<noOfThreads; i++) {
			threads[i].start();
		}
		for(int i=0; i<threads.length; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		long stop = System.currentTimeMillis();
		long duration = stop-start;
		System.out.println("The time taken for Bakery algorithm is " + duration + " ms");
		System.out.println("Number of successful locks (Bakery): " + Bakery.getCounter());
		
		
		
		BottomupTreeArray b = new BottomupTreeArray(noOfThreads);
		int[] nArr = new int[noOfThreads];
		for(int i=0; i<noOfThreads; i++) {
			nArr[i] = i+1;
		}
		Node root = b.CreateBTree(nArr, 0, noOfThreads-1);
		ArrayList<Node> array = b.TreeToArr(root);
		Thread[] tThreads = new Thread[noOfThreads];
		try {
			for(int i=0; i<noOfThreads; i++) {
				tThreads[i] = new IncrementCounterT(array, String.valueOf(i+1));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		long tStart = System.currentTimeMillis();
		for(int i=0; i<noOfThreads; i++) {
			tThreads[i].start();
		}
		for(int i=0; i<tThreads.length; i++) {
			try {
				tThreads[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		long tStop = System.currentTimeMillis();
		long tDuration = tStop-tStart;
		System.out.println("The time taken for Tournament algorithm is " + tDuration + " ms");
		System.out.println("Number of successful locks (Tournament): " + Peterson.getTournamentCounter());
		
		
	}
}
