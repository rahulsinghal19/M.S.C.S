import java.util.PriorityQueue;

//This class Quorum maintains a priority queue to queue all the request messages.
//Also keeps track of last request received and last response sent to a process.

public class Quorum {

	private PriorityQueue<Message> q = new PriorityQueue<>();
	
	private Message lastRequest = null;

	public boolean isLocked() {
		return lastRequest != null;
	}

	public Message getLastRequest() {
		return lastRequest;
	}

	public void setLastRequest(Message lastRequest) {
		this.lastRequest = lastRequest;
	}

	public void add(Message msg) {
		q.add(msg);
	}

	public Message pop() {
		return q.poll();
	}

	public boolean hasPending() {
		return !q.isEmpty();
	}

}
