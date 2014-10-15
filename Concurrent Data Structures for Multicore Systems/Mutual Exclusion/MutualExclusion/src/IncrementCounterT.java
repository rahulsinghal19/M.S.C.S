import java.util.ArrayList;
import java.util.Stack;


public class IncrementCounterT extends Thread {
	private static final int MAX = MutualExclusionTest.noOfOperations;
	private ArrayList<Node> array;
	private Stack<Integer> s = new Stack<>();

	public IncrementCounterT(ArrayList<Node> array, String name) {
//		System.out.println("Here");
		this.array = array;
		this.setName(name);

	}

	public void run() {

		// TODO Auto-generated constructor stub
		int me = Integer.parseInt(Thread.currentThread().getName());
		int to = -1;
		
		int tmp = (me % (MutualExclusionTest.noOfThreads + 1));
		int y = 0;
		for(int i=1; i<=tmp; i++) {
			if(i > 2 && (i%2==0) && ((i+1)%2 != 0)) {
				y++;
			}
		}
		to = (array.size()/2) + y;
//		System.out.println(to);
/*
		if(me == 1  || me == 2) {
			to = array.size() / 2;
		} else if(me == 3 || me == 4) {
			to = (array.size() / 2) + 1;
		} else if(me == 5 || me == 6) {
			to = (array.size() / 2) + 2;
		} else if(me == 7 || me == 8) {
			to = (array.size() / 2) + 3;
		} else if(me == 9 || me == 10) {
			to = (array.size() / 2) + 4;
		} else if(me == 11 || me == 12) {
			to = (array.size() / 2) + 5;
		} else if(me == 13 || me == 14) {
			to = (array.size() / 2) + 6;
		} else if(me == 15 || me == 16) {
			to = (array.size() / 2) + 7;
		}
*/
		for(int i = 0;i < MAX; i++ ) {	
			
			int x = (int) (Math.log(array.size()+1) / Math.log(2));
			for(int j=x; j>0; j--) {
				array.get(to).lock.lock();
				s.push(to);
				//compute its parent after acquiring its lock
				to = to /2;
			}
			
			//Critical section
			Peterson.setTournamentCounter(Peterson.getTournamentCounter() + 1);
			
			for(int j=0; j<x; j++) {
				to = s.pop();
				array.get(to).lock.unlock();
			}
			
			try {
				sleep(MutualExclusionTest.interRequestDelay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
