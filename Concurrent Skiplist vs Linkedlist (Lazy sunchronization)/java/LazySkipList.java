import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class LazySkipList {
	static final int MAX_LEVEL = 5;
	final SkipNode head = new SkipNode(Integer.MIN_VALUE);
	final SkipNode tail = new SkipNode(Integer.MAX_VALUE);
	static Random rand = new Random();

	public LazySkipList() {
		for (int i = 0; i < head.next.length; i++) {
			head.next[i] = tail;
		}
	}

	int find(Integer x, SkipNode[] preds, SkipNode[] succs) {
		int key = x.hashCode();
		int lFound = -1;
		SkipNode pred = head;
		for (int level = MAX_LEVEL; level >= 0; level--) {
			SkipNode curr = pred.next[level];
			while (key > curr.key) {
				pred = curr;
				curr = pred.next[level];
			}
			if (lFound == -1 && key == curr.key) {
				lFound = level;
			}
			preds[level] = pred;
			succs[level] = curr;
		}
		return lFound;
	}

	boolean add(Integer x) {
		int topLevel = randomLevel();
		SkipNode[] preds = new SkipNode[MAX_LEVEL + 1];
		SkipNode[] succs = new SkipNode[MAX_LEVEL + 1];
		while (true) {
			int lFound = find(x, preds, succs);
			if (lFound != -1) {
				SkipNode nodeFound = succs[lFound];
				if (!nodeFound.marked) {
					while (!nodeFound.fullyLinked) {
					}
					return false; // error 1, it was a line above
				}
				continue;
			}
			int highestLocked = -1;
			try {
				SkipNode pred, succ;
				boolean valid = true;
				for (int level = 0; valid && (level <= topLevel); level++) {
					pred = preds[level];
					succ = succs[level];
					pred.lock.lock();
					highestLocked = level;
					valid = !pred.marked && !succ.marked
							&& pred.next[level] == succ;
				}
				if (!valid)
					continue;
				SkipNode newNode = new SkipNode(x, topLevel);
				for (int level = 0; level <= topLevel; level++)
					newNode.next[level] = succs[level];
				for (int level = 0; level <= topLevel; level++)
					preds[level].next[level] = newNode;
				newNode.fullyLinked = true; // successful add linearization
											// point
				return true;
			} finally {
				for (int level = 0; level <= highestLocked; level++)
					preds[level].lock.unlock();
			}
		}
	}

	boolean remove(Integer x) {
		SkipNode victim = null;
		boolean isMarked = false;
		int topLevel = -1;
		SkipNode[] preds = new SkipNode[MAX_LEVEL + 1];
		SkipNode[] succs = new SkipNode[MAX_LEVEL + 1];
		while (true) {
			int lFound = find(x, preds, succs);
			if (lFound != -1)
				victim = succs[lFound];
			if (isMarked
					|| (lFound != -1 && (victim.fullyLinked
							&& victim.topLevel == lFound && !victim.marked))) {
				if (!isMarked) {
					topLevel = victim.topLevel;
					victim.lock.lock();
					if (victim.marked) {
						victim.lock.unlock();
						return false;
					}
					victim.marked = true;
					isMarked = true;
				}
				int highestLocked = -1;
				try {
					SkipNode pred, succ;
					boolean valid = true;
					for (int level = 0; valid && (level <= topLevel); level++) {
						pred = preds[level];
						pred.lock.lock();
						highestLocked = level;
						valid = !pred.marked && pred.next[level] == victim;
					}
					if (!valid)
						continue;
					for (int level = topLevel; level >= 0; level--) {
						preds[level].next[level] = victim.next[level];
					}
					victim.lock.unlock();
					return true;
				} finally {
					for (int i = 0; i <= highestLocked; i++) {
						preds[i].lock.unlock();
					}
				}
			} else
				return false;
		}
	}

	boolean contains(Integer x) {
		SkipNode[] preds = new SkipNode[MAX_LEVEL + 1];
		SkipNode[] succs = new SkipNode[MAX_LEVEL + 1];
		int lFound = find(x, preds, succs);
		return (lFound != -1 && succs[lFound].fullyLinked && !succs[lFound].marked);
	}

	private static int randomLevel() {
		double p = 0.5;
		int k = 0;
		int arrSize = (int) Math.pow(2, ((MAX_LEVEL - 1) + 2));
		int[] arr = new int[arrSize - 1];
		for (int i = 0; i <= MAX_LEVEL; i++) {
			int times = (int) ((p / (Math.pow(2, i))) * arrSize);
			for (int j = 0; j < times; j++) {
				arr[k++] = i;
			}
		}
		int val = rand.nextInt(arrSize - 1);
		return arr[val];
	}

	// return 0 if you find a node's value not existing in the higher levels in
	// comparison to the lower levels
	// simultaneously check if each level has an increasing order of
	// non-repeating elements
	// return -1 if the above property is not satisfied
	// if all the above conditions are met, then return the list count at level
	// 0 (which is correct)
	int ListCount() {
		HashSet<Integer> universal = new HashSet<>();
		int count = 0;
		SkipNode curr = null;
		for (int level = 0; level <= MAX_LEVEL; level++) {
			curr = head.next[level];
			while (curr != tail) {
				if (level == 0) {
					universal.add(curr.key);
					count++;
				} else {
					if (!universal.contains(curr.key)) {
						return 0;
					}
				}
				SkipNode next = curr.next[level];

				if (curr.key < next.key && curr.key != next.key) {
					curr = curr.next[level];
					next = next.next[level];
				} else {
					return -1;
				}
			}
		}

		return count;
	}

	private static final class SkipNode {
		final ReentrantLock lock = new ReentrantLock();
		final Integer item;
		final int key;
		final SkipNode[] next;
		volatile boolean marked = false;
		volatile boolean fullyLinked = false;
		private int topLevel;

		// sentinal node constructor
		public SkipNode(int key) {
			this.item = null;
			this.key = key;
			next = new SkipNode[MAX_LEVEL + 1];
			topLevel = MAX_LEVEL;
		}

		public SkipNode(Integer x, int height) {
			item = x;
			key = x.hashCode();
			next = new SkipNode[height + 1];
			topLevel = height;
		}

		public void lock() {
			lock.lock();
		}

		public void unlock() {
			lock.unlock();
		}
	}

}
