import java.util.concurrent.locks.ReentrantLock;

public class Node {
	int key;
	Node next;
	ReentrantLock lock;
	volatile boolean marked = false;

	public Node(int key) {
		this.key = key;
		next = null;
		lock = new ReentrantLock();
	}
}
