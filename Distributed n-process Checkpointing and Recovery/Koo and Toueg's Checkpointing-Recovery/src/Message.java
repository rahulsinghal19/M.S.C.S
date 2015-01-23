//Message Class
//This class is used to define the various types of messages used in our project. 

import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable, Comparable<Message> {

	public enum Type {
		APP_COUNT,REQUEST_CHECKPOINT,ACK_CHECKPOINT,NACK_CHECKPOINT,TAKE_CHECKPOINT,TOKEN,TERMINATE, REQUEST_RECOVERY, ACK_RECOVERY, ROLLBACK,MONITOR_RECORD_CHECKPOINT,MONITOR_TERMINATE,APP_TERMINATE
	}

	private int from, to, label;
	private Serializable payload;
	private Type type;

	public Message(int from, int to, int label, Type type, Serializable payload) {
		this.from = from;
		this.to = to;
		this.label = label;
		this.type = type;
		this.payload = payload;
	}
	
	public Message(int from, int to, Type type,Serializable payload) {
		this.from = from;
		this.to = to;
		this.label = -1;
		this.type = type;
		this.payload = payload;
	}

	public Serializable getPayload() {
		return payload;
	}

	public int from() {
		return from;
	}

	public int to() {
		return to;
	}

	public int label() {
		return label;
	}

	public Type type() {
		return type;
	}

	@Override
	public int compareTo(Message o) {
		return Integer.compare(label, o.label);
	}

	@Override
	public String toString() {
		return "Message [from=" + from + ", to=" + to + ", label=" + label
				+ ", payload=" + payload + ", type=" + type + "]";
	}

}
