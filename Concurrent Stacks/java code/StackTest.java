import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;


public class StackTest {
	
	volatile static int pushOperation = 0;
	volatile static int popOperation = 0;
	static int[] fin;
	public static int elimination;
	
	public static void main(String[] args) {
		int numberOfThreads = Integer.parseInt(args[0]);
		int numberOfOperations = Integer.parseInt(args[1]);
		
		fin = new int[numberOfThreads];

		LockStack lockStack = new LockStack();
		Thread[] lThread = new Thread[numberOfThreads];
		for(int i=0; i<numberOfThreads; i++) {
			lThread[i] = new LockStackThread(lockStack, numberOfOperations, i);
		}
		long lStart = System.nanoTime();
		for(int i=0; i<numberOfThreads; i++) {
			lThread[i].start();
		}
		for(int i=0; i<numberOfThreads; i++) {
			try {
				lThread[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long lStop = System.nanoTime();
		long duration = lStop - lStart;
		int sum = 0;
//		for (int f : finish) {
//			sum += f;
//		}
		
		for(int i=0; i<fin.length; i++) {
			sum += fin[i];
		}
		
		System.out.println("Sum from operations : " + sum);
		int pop = 0;
		while(!lockStack.isEmpty()) {
			pop += lockStack.pop();
		}
		System.out.println("Value from stack " + pop);
		System.out.println("Difference of all sum & pop'd values " + (sum - pop));
		
		System.out.println(lockStack);
		System.out.println("Lock stack duration : " + duration + " ms");
		System.out.println("Push Operations : " + pushOperation);
		System.out.println("Pop Operations : " + popOperation);
		System.out.println("Total Operations : " + (pushOperation + popOperation));
		System.out.println((pushOperation + popOperation)/(double)(duration / 1000000) + " ops/ms");
		System.out.println("-------------------------------------------------------------------------------------");
		//-------------------------------------------------------------------------------------
		
		pushOperation = 0;
		popOperation = 0;
		
		EliminationLockStack<Integer> eliminationLockStack = new EliminationLockStack<Integer>(1, 20);
		for(int i=0; i<numberOfThreads; i++) {
			lThread[i] = new ExchangerThread(eliminationLockStack, numberOfOperations);
		}
		long elFStart = System.nanoTime();
		for(int i=0; i<numberOfThreads; i++) {
			lThread[i].start();
		}
		for(int i=0; i<numberOfThreads; i++) {
			try {
				lThread[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long elFStop = System.nanoTime();
		duration = elFStop - elFStart;
		
		System.out.println("Exchange Lock stack duration : " + duration + " ms");
		System.out.println("Push Operations : " + pushOperation);
		System.out.println("Pop Operations : " + popOperation);
		System.out.println("Eliminated : " + elimination);
		System.out.println("Total Operations : " + (pushOperation + popOperation + elimination));
		System.out.println((pushOperation + popOperation + elimination)/(double)(duration / 1000000) + " ops/ms");
		//-------------------------------------------------------------------------------------
		/*
		pushOperation = 0;
		popOperation = 0;
		LockFreeStack lockFreeStack = new LockFreeStack();
		for(int i=0; i<numberOfThreads; i++) {
			lThread[i] = new LockFreeStackThread(lockFreeStack, numberOfOperations);
		}
		long lFStart = System.currentTimeMillis();
		for(int i=0; i<numberOfThreads; i++) {
			lThread[i].start();
		}
		for(int i=0; i<numberOfThreads; i++) {
			try {
				lThread[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long lFStop = System.currentTimeMillis();
		duration = lFStop - lFStart;
		System.out.println("Lock stack duration : " + duration + " ms");
		System.out.println("Total operations : " + aInt.get());
		System.out.println("Push Operations : " + pushOperation);
		System.out.println("Pop Operations : " + popOperation);
		System.out.println("Total Operations : " + (pushOperation + popOperation));
		*/
	}
}
