import java.nio.ByteBuffer;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;


public class ReceivingThread implements Runnable {
	int msgSize = 1024;
	MessageInfo messageInfo;
	SctpChannel sctpChannel;
	ByteBuffer byteBuffer = ByteBuffer.allocate(msgSize);
	String message = null;

	public ReceivingThread(SctpChannel sctpChannel) {
		// TODO Auto-generated constructor stub
		this.sctpChannel = sctpChannel;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			messageInfo = sctpChannel.receive(byteBuffer, null, null);
			//Figure out what to do with the received byteBuffer!
			message = byteToString(byteBuffer);
			System.out.println("Msg@@@@@@@@@@: " + message);
			SumDistributed.ProcessData(message);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		
	}

	public String byteToString(ByteBuffer byteBuffer)
	{
		byteBuffer.position(0);
		byteBuffer.limit(msgSize);
		byte[] bufArr = new byte[byteBuffer.remaining()];
		byteBuffer.get(bufArr);
		return new String(bufArr);
	}

}
