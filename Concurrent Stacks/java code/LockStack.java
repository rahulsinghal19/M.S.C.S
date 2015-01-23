import java.util.concurrent.locks.ReentrantLock;

public class LockStack<T> {
	int bottom = -999;
	Node<T> top;
	ReentrantLock lock = new ReentrantLock();

	public LockStack() {
		top = null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	void push(int x) {
		Node temp = new Node(x);
		temp.next = top;
		top = temp;
	}

	@SuppressWarnings("unchecked")
	int pop() {
		int x;
		if (top != null) {
			x = (Integer) top.value;
			top = top.next;
		} else {
			x = bottom;
		}
		return (int) x;
	}
	
	boolean isEmpty() {
		return top == null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	boolean tryPush(T x) {
		if (lock.tryLock()) {
			Node temp = new Node(x);
			temp.next = top;
			top = temp;
			StackTest.pushOperation++;
//			System.out.println("Pushed " + x);
			lock.unlock();
			return true;
		}
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	Node tryPop() {
		if(lock.tryLock()) {
			Node x;
			if (top != null) {
				x = top;
				top = top.next;
			} else {
				x = new Node(Integer.MIN_VALUE);
			}
			StackTest.popOperation++;
//			System.out.println("Pop " + x.value);
			lock.unlock();
			return x;
		}
		return null;
	}
}
