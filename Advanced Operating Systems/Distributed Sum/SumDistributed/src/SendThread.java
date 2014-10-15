import java.nio.ByteBuffer;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;


public class SendThread implements Runnable {
	
	SctpChannel sctpChannel;
	String sendToken;
	int msgSize = 1024;
	MessageInfo messageInfo;
	ByteBuffer byteBuffer = ByteBuffer.allocate(msgSize);

	public SendThread(SctpChannel sctpChannel, String sendToken) {
		// TODO Auto-generated constructor stub
		this.sctpChannel = sctpChannel;
		this.sendToken = sendToken;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			messageInfo = MessageInfo.createOutgoing(null, 0);
			byteBuffer.put(sendToken.getBytes());
			byteBuffer.flip();
			sctpChannel.send(byteBuffer, messageInfo);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		
	}

}
