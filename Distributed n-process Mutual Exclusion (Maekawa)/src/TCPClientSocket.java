// This class is created to handle the TCP Client Sockets functions

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class TCPClientSocket implements ClientSocketHandler {

	private Socket sock;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	public TCPClientSocket(Socket s) throws Exception {
		this.sock = s;
		this.oos = new ObjectOutputStream(sock.getOutputStream());
		this.ois = new ObjectInputStream(sock.getInputStream());
	}

	@Override
	public void connect(String host, int port) throws Exception {
		this.sock = new Socket(host, port);
		this.oos = new ObjectOutputStream(sock.getOutputStream());
		this.ois = new ObjectInputStream(sock.getInputStream());
	}

	@Override
	public void close() throws Exception {
		if (sock != null)
			sock.close();
		sock = null;
	}

	@Override
	public void sendObject(Serializable o) throws Exception {
		oos.writeObject(o);
		oos.flush();
	}

	@Override
	public Serializable receiveObject() throws Exception {
		return (Serializable) ois.readObject();
	}

}
