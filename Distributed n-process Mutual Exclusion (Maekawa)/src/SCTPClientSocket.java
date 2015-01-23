//This class is used for SCTP socket connection for client socket.

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;

public class SCTPClientSocket implements ClientSocketHandler {

	private ByteBuffer buffer = ByteBuffer.allocate(1024 * 5);
	private SctpChannel sock;

	public SCTPClientSocket(SctpChannel socket) {
		this.sock = socket;
	}

	public SCTPClientSocket(String host, int port) throws Exception {
		connect(host, port);
	}

	@Override
	public void connect(String host, int port) throws Exception {
		SocketAddress socketAddress = new InetSocketAddress(host, port);
		sock = SctpChannel.open();
		sock.bind(new InetSocketAddress(port + 100)); // use same port as server.
		sock.connect(socketAddress);
	}

	@Override
	public void close() throws Exception {
		if (sock != null)
			sock.close();
		sock = null;
	}

	@Override
	public void sendObject(Serializable o) throws Exception {
		buffer.clear();
		buffer.put(ByteUtil.toByteArray(o));
		buffer.flip();
		MessageInfo mi = MessageInfo.createOutgoing(null, 0);
		sock.send(buffer, mi);
	}

	@Override
	public Serializable receiveObject() throws Exception {
		buffer.clear();
		sock.receive(buffer, null, null);
		buffer.flip();
		return ByteUtil.toObject(buffer.array());
	}

}
