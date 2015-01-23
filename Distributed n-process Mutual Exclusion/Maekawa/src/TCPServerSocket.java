// This interface is created to handle the TCP server Sockets functions

import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerSocket implements ServerSocketHandler {

	private ServerSocket ss;

	@Override
	public void listen(int port) throws Exception {
		ss = new ServerSocket(port);
	}

	@Override
	public ClientSocketHandler accept() throws Exception {
		return new TCPClientSocket(ss.accept());
	}

	@Override
	public void close() throws Exception {
		ss.close();
	}

	@Override
	public ClientSocketHandler makeSocket(String host, int port)
			throws Exception {
		return new TCPClientSocket(new Socket(host, port));
	}

}
