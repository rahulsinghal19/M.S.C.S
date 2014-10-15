import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;


public class Peterson implements Lock {

	volatile boolean[] flag;
	volatile int victim;
	private static int tournamentCounter = 0;
	
	public Peterson() {
		// TODO Auto-generated constructor stub
		flag = new boolean[2];
	}

	@Override
	public void lock() {
		// TODO Auto-generated method stub
		Thread currentThread = Thread.currentThread();
		int me = (Integer.parseInt(currentThread.getName()) + 1)%2;
		int other = 1 - me;
		flag[me] = true;
		victim = me;
		while((flag[other] == true) && victim == me) {
			//Thread waiting to acquire lock
		}
//		setTournamentCounter(getTournamentCounter() + 1);
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean tryLock() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit)
			throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void unlock() {
		// TODO Auto-generated method stub
		Thread currentThread = Thread.currentThread();
		int me = (Integer.parseInt(currentThread.getName()) + 1)%2;
		flag[me] = false;
	}

	@Override
	public Condition newCondition() {
		// TODO Auto-generated method stub
		return null;
	}

	public static int getTournamentCounter() {
		return tournamentCounter;
	}

	public static void setTournamentCounter(int tournamentCounter) {
		Peterson.tournamentCounter = tournamentCounter;
	}
}
