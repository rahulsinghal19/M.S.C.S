// This interface is created to handle the Client Sockets functions

import java.io.Serializable;

public interface ClientSocketHandler {

	public void connect(String host, int port) throws Exception;
	
	public void close() throws Exception;

	public void sendObject(Serializable o) throws Exception;

	public Serializable receiveObject() throws Exception;
	
}
