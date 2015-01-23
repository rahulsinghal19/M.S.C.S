//ClientSocketHandler Interface
//This interface is used by other class when it has to create and manipulate Client socket

import java.io.Serializable;

public interface ClientSocketHandler {

	public void connect(String host, int port) throws Exception;
	
	public void close() throws Exception;

	public void sendObject(Serializable o) throws Exception;

	public Serializable receiveObject() throws Exception;
	
}
