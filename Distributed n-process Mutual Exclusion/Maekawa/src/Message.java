import java.io.Serializable;

//This class include all the different types of Messages used in our implementation.

public class Message implements Serializable, Comparable<Message> {

	public enum Type {
		Q_REQUEST, Q_GRANT, Q_RELEASE, Q_INQUIRE, Q_FAILED, Q_YIELD,
		MONITOR_ENTERCS,MONITOR_LEAVECS, DONE, TERMINATE,
		MONITOR_ENTERCS_ACK,MONITOR_LEAVECS_ACK
	}

	private final int from, to, timestamp;

	private final Type type;
	private final Serializable payload;

	public Message(int from, int to, int timestamp, Type type,
			Serializable payload) {
		super();
		this.from = from;
		this.to = to;
		this.type = type;
		this.payload = payload;
		this.timestamp = timestamp;
	}

	public Message(int from, int to, int timestamp, Type type) {
		this(from, to, timestamp, type, null);
	}

	public int getTimestamp() {
		return timestamp;
	}

	public int from() {
		return from;
	}

	public int to() {
		return to;
	}

	public Type getType() {
		return type;
	}

	public Serializable getPayload() {
		return payload;
	}

	@Override
	public String toString() {
		if (payload == null)
			return "Message [from=" + from + ", to=" + to + ", timestamp="
					+ timestamp + ", type=" + type + "]";
		else
			return "Message [from=" + from + ", to=" + to + ", timestamp="
					+ timestamp + ", type=" + type + ", payload=" + payload
					+ "]";
	}

	@Override
	public int compareTo(Message o) {
		int cmp = Integer.compare(timestamp, o.timestamp);
		if (cmp == 0)
			return Integer.compare(from, o.from);
		return cmp;
	}

}
