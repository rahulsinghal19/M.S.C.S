import java.util.Random;

public class LazyLinkedListThread extends Thread {

	LazyLinkedList list;
	int numberOfOperations;
	Random rand = new Random();
	int op;
	int key;
	int keySize;
	int insert;
	int delete;
	int search;
	int searchRange;
	int insertRange;
	int rangeSize;

	public LazyLinkedListThread(LazyLinkedList list, int numberOfOperations,
			int keySize, int insert, int delete, int search) {
		this.list = list;
		this.numberOfOperations = numberOfOperations;
		this.keySize = keySize;
		this.insert = insert;
		this.delete = delete;
		this.search = search;
		this.searchRange = this.search;
		this.insertRange = this.insert + this.search;
		this.rangeSize = this.search + this.insert + this.delete;
	}

	@Override
	public void run() {
		for (int i = 0; i < numberOfOperations; i++) {
			op = rand.nextInt(rangeSize);
			if (op < searchRange) {
				op = 0;
			} else if (op < insertRange) {
				op = 1;
			} else {
				op = 2;
			}

			key = rand.nextInt(keySize);
			switch (op) {
			case 0:
				list.contains(key);
				break;
			case 1:
				boolean returnResultAdd = list.add(key);
				if (returnResultAdd) {
					ListTest.add.getAndIncrement();
				}
				break;
			case 2:
				boolean returnResultRemove = list.remove(key);
				if (returnResultRemove) {
					ListTest.del.getAndIncrement();
				}
				break;
			default:
				System.out.println("Invalid operation");
				break;
			}
		}
	}

}
