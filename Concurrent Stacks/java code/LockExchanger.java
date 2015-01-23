import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class EliminationLockStack<T> extends LockStack {
	private EliminationArray<T> eliminationArray;
	private static ThreadLocal<RangePolicy> policy;
	private int exchangerCapacity, exchangerWaitDuration;

	EliminationLockStack(final int exchangerCapacity, int exchangerWaitDuration) {
		this.exchangerCapacity = exchangerCapacity;
		this.exchangerWaitDuration = exchangerWaitDuration;
		eliminationArray = new EliminationArray<T>(exchangerCapacity,
				exchangerWaitDuration);
		policy = new ThreadLocal<RangePolicy>() {
			protected synchronized RangePolicy initialValue() {
				return new RangePolicy(exchangerCapacity);
			}
		};
	}

	public String getStackInfo() {
		return exchangerCapacity + "," + exchangerWaitDuration;
	}

	@SuppressWarnings("unchecked")
	public void Push(T value) {
		RangePolicy rangePolicy = policy.get();
		Node<T> node = new Node<T>(value);

		while (true) {
			if (this.tryPush(value)) {
				return;
			} else {
				try {
//					System.out.println("Trying to eliminate for push..");
					T otherValue = eliminationArray.visit(value,
							rangePolicy.getRange());
					if (otherValue == null) { //push p
						rangePolicy.recordEliminationSuccess();
//						System.out.println("Eliminated at push " + value);
						StackTest.elimination++;
						return;
					}
				} catch (TimeoutException e) {
					rangePolicy.recordEliminationTimeout();
//					System.out.println("Timed out at Push " + value);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public T Pop() throws Exception {
		RangePolicy rangePolicy = policy.get();

		while (true) {
			Node<T> returnNode = tryPop();
			if (returnNode != null) {
				return returnNode.value;
			} else {
				try {
//					System.out.println("Trying to eliminate for pop..");
					T otherValue = eliminationArray.visit(null,
							rangePolicy.getRange());
					if (otherValue != null) {
						rangePolicy.recordEliminationSuccess();
						StackTest.elimination++;
//						System.out.println("Eliminated at pop " + otherValue);
						return otherValue;
					}
				} catch (TimeoutException e) {
					rangePolicy.recordEliminationTimeout();
//					System.out.println("Timed out at pop ");
				}
			}
		}
	}
}

class LockExchanger<T> {
	static final int EMPTY = 0, WAITING = 1, BUSY = 2;
	// AtomicStampedReference<T> slot = new AtomicStampedReference<T>(null, 0);
	ReentrantLock lock = new ReentrantLock();
	Condition condition = lock.newCondition();
	int status = EMPTY;
	T yrItem;

	public T exchange(T myItem, long timeout, TimeUnit unit)
			throws TimeoutException {
		long nanos = unit.toNanos(timeout);
		long timeBound = System.nanoTime() + nanos;

		while (true) {
			if (System.nanoTime() > timeBound)
				throw new TimeoutException();
			lock.lock();
			switch (status) {
			case EMPTY:
//				System.out.println("Empty..");
				yrItem = myItem;
				status = WAITING;
				try {
					synchronized (condition) {
						condition.awaitNanos(timeBound - System.nanoTime());
					}
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (status == WAITING) {
					status = EMPTY;
					lock.unlock();
					throw new TimeoutException();
				}

				if (status == BUSY) {
					status = EMPTY;
					lock.unlock();
					return yrItem;
				}
				break;

			case WAITING:
//				System.out.println("Waiting..");
//				lock.lock();
				T tmpItem = yrItem;
				yrItem = myItem;
				status = BUSY;
				lock.unlock();
				synchronized (condition) {
					condition.notify();
				}
				
				return tmpItem;

			case BUSY:
				break;

			default: // impossible;
				// ...
			}
			lock.unlock();
		}
	}
}

class EliminationArray<T> {
	private int duration = 10;
	private Random random;
	private LockExchanger<T>[] exchanger;

	@SuppressWarnings("unchecked")
	EliminationArray(int capacity, int duration) {
		this.duration = duration;
		exchanger = (LockExchanger<T>[]) new LockExchanger[capacity];
		for (int i = 0; i < capacity; i++) {
			exchanger[i] = new LockExchanger<T>();
		}
		random = new Random();
	}

	public T visit(T value, int range) throws TimeoutException {
		int slot = random.nextInt(range);
		return (exchanger[slot]
				.exchange(value, duration, TimeUnit.MILLISECONDS));
	}
}
