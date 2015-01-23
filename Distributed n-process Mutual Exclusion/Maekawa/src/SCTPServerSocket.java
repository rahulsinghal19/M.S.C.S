//This class is used for SCTP socket connection for server socket.

import java.net.InetSocketAddress;

import com.sun.nio.sctp.SctpServerChannel;

public class SCTPServerSocket implements ServerSocketHandler {

	private SctpServerChannel ss;

	@Override
	public void listen(int port) throws Exception {
		ss = SctpServerChannel.open();
		ss.bind(new InetSocketAddress(port));
	}

	@Override
	public ClientSocketHandler accept() throws Exception {
		return new SCTPClientSocket(ss.accept());
	}

	@Override
	public void close() throws Exception {

		if (ss != null)
			ss.close();
		ss = null;
	}

	@Override
	public ClientSocketHandler makeSocket(String host, int port)
			throws Exception {
		return new SCTPClientSocket(host, port);
	}

}
