import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;


public class Bakery implements Lock {
	
	volatile boolean[] flag;
	volatile int[] label;
	private int threadCount;
	private static int counter = 0;
	
	public Bakery(int n) {
		threadCount = n;
		flag = new boolean[n];
		label = new int[n];
		for(int i=0; i<n; i++) {
			flag[i] = false;
			label[i] = 0;
		}
	}

	@Override
	public void lock() {
		Thread currentThread = Thread.currentThread();
		int me = Integer.parseInt(currentThread.getName());
		flag[me] = true;
		label[me] = (maxValue(label) + 1);
		
		for(int other=0; other<threadCount; other++) {
			if(other != me) {
				while(flag[other] && (label[other] < label[me] || ( (label[other] < label[me]) && other < me) )) {
					//Thread waiting to acquire lock
				}
			}
		}
		setCounter(getCounter() + 1); //Critical section
	}

	private int maxValue(int[] label) {
		int max = label[0];
		for(int k=0; k<label.length; k++) {
			if(label[k] > max) {
				max = label[k];
			}
		}
		return max;
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
		Thread currentThread = Thread.currentThread();
		int me = Integer.parseInt(currentThread.getName());
		flag[me] = false;
	}

	@Override
	public Condition newCondition() {
		// TODO Auto-generated method stub
		return null;
	}

	public static int getCounter() {
		return counter;
	}

	public static void setCounter(int counter) {
		Bakery.counter = counter;
	}
	
	

}
