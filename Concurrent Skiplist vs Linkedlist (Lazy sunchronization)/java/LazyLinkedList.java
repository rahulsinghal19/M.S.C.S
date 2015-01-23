public class LazyLinkedList {

	Node head;
	Node tail;

	public LazyLinkedList() {
		head = new Node(Integer.MIN_VALUE);
		tail = new Node(Integer.MAX_VALUE);
		head.next = tail;
	}

	public boolean traverse() {
		boolean ok = false;
		Node curr = head.next;
		Node next = curr.next;
		while (curr != tail) {
			if (curr.key < next.key && curr.key != next.key) {
				curr = curr.next;
				next = next.next;
				ok = true;
			} else {
				ok = false;
				break;
			}
		}
		return ok;
	}

	public boolean add(int key) {
		Node pred, curr;
		while (true) {
			pred = head;
			curr = pred.next;
			while (curr.key < key) {
				pred = curr;
				curr = curr.next;
			}
			try {
				pred.lock.lock();
				curr.lock.lock();
				if (validate(pred, curr)) {
					if (curr.key == key) {
						return false;
					} else {
						Node newNode = new Node(key);
						newNode.next = curr;
						pred.next = newNode;
						// ListTest.add.getAndIncrement();
						return true;
					}
				}
			} finally {
				curr.lock.unlock();
				pred.lock.unlock();
			}
		}
	}

	public boolean remove(int key) {
		Node pred, curr;
		while (true) {
			pred = head;
			curr = pred.next;
			while (curr.key < key) {
				pred = curr;
				curr = curr.next;
			}
			try {
				pred.lock.lock();
				curr.lock.lock();
				if (validate(pred, curr)) {
					if (curr.key == key) {
						curr.marked = true;
						pred.next = curr.next;
						// ListTest.del.getAndIncrement();
						return true;
					} else {
						return false;
					}
				}
			} finally {
				curr.lock.unlock();
				pred.lock.unlock();
			}
		}
	}

	public boolean contains(int key) {
		Node curr = head;
		while (curr.key < key) {
			curr = curr.next;
		}
		return curr.key == key && !curr.marked;
	}

	private boolean validate(Node pred, Node curr) {
		return (!pred.marked && !curr.marked && (pred.next == curr));
	}

}
