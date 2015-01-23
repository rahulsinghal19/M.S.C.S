// This interface is created to handle the Server Sockets functions

public interface ServerSocketHandler {

	public void listen(int port) throws Exception;

	public ClientSocketHandler accept() throws Exception;

	public void close() throws Exception;
	
	public ClientSocketHandler makeSocket(String host,int port) throws Exception;

}
