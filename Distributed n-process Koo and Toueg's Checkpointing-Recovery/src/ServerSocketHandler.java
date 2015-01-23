//ServerSocketHandler Interface
//This interface is used by other class when it has to create and manipulate Server socket


public interface ServerSocketHandler {

	public void listen(int port) throws Exception;

	public ClientSocketHandler accept() throws Exception;

	public void close() throws Exception;
	
	public ClientSocketHandler makeSocket(String host,int port) throws Exception;

}
