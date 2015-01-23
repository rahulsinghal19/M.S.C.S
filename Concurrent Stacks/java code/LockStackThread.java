import java.util.Random;

public class LockStackThread extends Thread {
	
	int n;
	volatile int count = 0;
	Random rand = new Random();
	@SuppressWarnings("rawtypes")
	LockStack stack;
	int name;

	@SuppressWarnings("rawtypes")
	public LockStackThread(LockStack lockStack, int numberOfOperations,int name) {
		n = numberOfOperations;
		stack = lockStack;
		this.name = name;
//		this.setName(String.valueOf(name));
	}

	public void run() {
		
		for(int i=0; i<n; i++) {
			int op = rand.nextInt(2);
			if(op == 0) {
				//push
				stack.lock.lock();
				int x = rand.nextInt(10);
//				System.out.println(x);
				stack.push(x);
				StackTest.pushOperation++;
				count = count + x;
				stack.lock.unlock();
			} else {
				//pop
				stack.lock.lock();
				int val = stack.pop();
//				System.out.println("val " + val);
				StackTest.popOperation++;
				if(val >= 0) {
					count = count - val;
				}
				stack.lock.unlock();
			}
		}
//		System.out.println(currentThread().getName() + " " + count);
		StackTest.fin[name] = count;
//		StackTest.finish.add(count);
	}
	
}
