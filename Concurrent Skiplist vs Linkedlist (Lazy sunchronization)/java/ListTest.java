import java.util.concurrent.atomic.AtomicInteger;

public class ListTest {

	public volatile static AtomicInteger addOps = new AtomicInteger();
	public volatile static AtomicInteger delOps = new AtomicInteger();

	public volatile static AtomicInteger add = new AtomicInteger();
	public volatile static AtomicInteger del = new AtomicInteger();

	public static void main(String[] args) {

		if (args.length < 6) {
			System.out
					.println("Error! Usage: java ListTest <Threads> <Number_of_operations> <Key_Size> <Search> <Insert> <Delete>");
		} else {

			LazyLinkedList list = new LazyLinkedList();
			LazyLinkedListThread[] lThread = null;

			int threads = Integer.parseInt(args[0]);
			int numberOfOperations = Integer.parseInt(args[1]);
			int keySize = Integer.parseInt(args[2]);
			int searchOps = Integer.parseInt(args[3]);
			int insertOps = Integer.parseInt(args[4]);
			int removeOps = Integer.parseInt(args[5]);

			lThread = new LazyLinkedListThread[threads];

			for (int i = 0; i < threads; i++) {
				lThread[i] = new LazyLinkedListThread(list, numberOfOperations,
						keySize, insertOps, removeOps, searchOps);
			}

			long start = System.nanoTime();
			for (int i = 0; i < threads; i++) {
				lThread[i].start();
			}

			try {
				for (int i = 0; i < threads; i++) {
					lThread[i].join();

				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long end = System.nanoTime();
			long duration = end - start;

			Node curr = list.head;
			int count = 0;
			while (curr.next != list.tail) {
				curr = curr.next;
				count++;
			}

			System.out.println("Successful Add operations " + add.get());
			System.out.println("Successful Remove operations " + del.get());
			System.out.println("Successful Atomic " + (add.get() - del.get()));

			System.out.println("Items in list " + count);

			boolean result = (list.traverse() && ((add.get() - del.get()) == count));

			System.out.println("Consistent list? " + result);
			System.out.println("Duration " + duration);
			System.out.println("Throughput : " + (numberOfOperations * threads)
					/ (double) (duration / 1000000.0));

			System.out
					.println("-------------------------------------------------");

			add.set(0);
			del.set(0);
			LazySkipList lList = new LazySkipList();
			LazySkipThread[] lSkipThread = null;

			lSkipThread = new LazySkipThread[threads];

			for (int i = 0; i < threads; i++) {
				lSkipThread[i] = new LazySkipThread(lList, numberOfOperations,
						keySize, insertOps, removeOps, searchOps);
			}

			start = System.nanoTime();
			for (int i = 0; i < threads; i++) {
				lSkipThread[i].start();
			}

			try {
				for (int i = 0; i < threads; i++) {
					lSkipThread[i].join();

				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			end = System.nanoTime();
			duration = end - start;

			System.out.println("Successful Add operations " + add.get());
			System.out.println("Successful Remove operations " + del.get());

			System.out.println("Unsuccessful Add ops " + addOps.get()
					+ " & Del ops " + delOps.get());

			System.out.println("Duration " + duration);
			System.out.println("Duration : " + (numberOfOperations * threads)
					/ (double) (duration / 1000000.0));
			System.out.println("Successful Atomic " + (add.get() - del.get()));
			result = (count = lList.ListCount()) == (add.get() - del.get());
			System.out.println("Items in the list : " + count);
			System.out.println("Consistent skip list? " + result);
		}
	}
}
