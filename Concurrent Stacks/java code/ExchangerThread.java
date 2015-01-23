import java.util.Random;


public class ExchangerThread extends Thread {
	int n;
	Random rand = new Random();
	EliminationLockStack<Integer> stack;

	
	
	public ExchangerThread(EliminationLockStack<Integer> eliminationLockStack,
			int numberOfOperations) {
		n = numberOfOperations;
		stack = eliminationLockStack;
	}



	public void run() {
		for(int i=0; i<n; i++) {
			int op = rand.nextInt(2);
			if(op == 0) {
				//push
				Integer x = rand.nextInt(10);
				stack.Push(x);
			} else {
				//pop
				try {
					stack.Pop();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println(e);
					e.printStackTrace();
				}
			}
		}
	}
}
