import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Random;

import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;


class SctpServerThread implements Runnable {
	
	int port;
	SctpChannel sctpChannel;
	static boolean serverRun = true;
	
	public SctpServerThread(int port) {
		// TODO Auto-generated constructor stub
		this.port = port;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			SctpServerChannel sctpServerChannel = SctpServerChannel.open();
			InetSocketAddress serverAddr = new InetSocketAddress(port);
			sctpServerChannel.bind(serverAddr);
			
			while(serverRun) {
				sctpChannel = sctpServerChannel.accept();
				
				try {
					ReceivingThread serverReceivingThread = new ReceivingThread(sctpChannel);
					Thread receivedMsgThread = new Thread(serverReceivingThread);
					receivedMsgThread.start();
					Thread.sleep(100);
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
}

public class SumDistributed {
	
	static int label = -1;
	static int finalSum = -1;
	static int done = 0;
	static int lineCount = 0;
	static int nodeId = 0;
	static ArrayList<String> address = new ArrayList<>();
	static ArrayList<Integer> port = new ArrayList<>();

	public static void main(String[] args) {
		
		SumDistributed d = new SumDistributed();
		int sum = -1;
		String[] seperatedPath = null;
		
		StringBuilder token = new StringBuilder();
		Random random = new Random();
		
		if(args.length != 1) {
			System.out.println("Usage : java SumDistributed nodeId");
		} else {
			nodeId = Integer.parseInt(args[0]);
			String fileName = new File("configuration.txt").getAbsolutePath();
			
			try {
				FileReader configFile = new FileReader(fileName);
				BufferedReader bufferReader = new BufferedReader(configFile);
				String line;
				
				while ((line = bufferReader.readLine()) != null) {
					lineCount++;
					String[] read = line.split(" ");

					//Add address & port of all the read item
					address.add(read[0]);
					port.add(Integer.parseInt(read[1]));

					//Token has information only about it's path    
					if (lineCount == nodeId) {
						seperatedPath = read[2].split(",");
					}
				}
				
			} catch(FileNotFoundException ex) {
				System.out.println("Configuration file not found! " + ex);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			
			for(int i=0; i<seperatedPath.length; i++) {
				int value = Integer.parseInt(seperatedPath[i]);
				String addrPort = address.get(value)+":"+port.get(value);
				token.append(addrPort);
				token.append(";");
			}
			
			label = random.nextInt(100);
			sum = 0;
			
			token.replace(token.lastIndexOf(";"), token.lastIndexOf(";")+1, "%");
			token.append(sum);
						
			StartServer(port.get(nodeId-1));
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			new Thread(new MonitoringThread()).start();
			
			ProcessData(token.toString());
			
		}
	}

	private static void StartServer(int port) {
		// TODO Auto-generated method stub
		SctpServerThread serverThread = new SctpServerThread(port);
		Thread t = new Thread(serverThread);
		t.start();
		
	}

	public static synchronized void ProcessData(String message){
		// TODO Auto-generated method stub
		
		System.out.println("Received message " + message); //All good
		try {
			String hostName = "";
			int port = 0;
			int sum = 0;
			
			if(message.contains("%") || message.contains(";")) {
				String[] withSum = message.split("%"); //withSum[1] has the sum
				String[] paths = withSum[0].split(";"); //all the paths
				
				StringBuilder sendToken = new StringBuilder();
				StringBuilder remainingToken = new StringBuilder();
				
				for(int i=0; i<paths.length; i++) {
					if(i==0) {
						String[] addrPort = paths[0].split(":");
						hostName = addrPort[0];
						port = Integer.parseInt(addrPort[1]);
					} else {
						remainingToken.append(paths[i]);
						remainingToken.append(";");
					}
				}

				InetSocketAddress socketAddress = new InetSocketAddress(hostName, port);
				SctpChannel sctpChannel;
				
				sctpChannel = SctpChannel.open();
				sctpChannel.connect(socketAddress);
				
				String currentSum = withSum[1];
//				System.out.println(currentSum + " Before adding.. Current sum..........");
				sum = Integer.parseInt(currentSum.trim());
//				System.out.println(sum + " After parsing.. Current sum..........");
				sum += SumDistributed.label;
				
				sendToken.append(remainingToken);
				
				if(remainingToken.length() == 0) {
					System.out.println("Empty remaining token"+remainingToken+".");
				}
				else {
					System.out.println("#########"+sendToken);
					sendToken.replace(sendToken.lastIndexOf(";"), sendToken.lastIndexOf(";")+1, "%");
				}
				sendToken.append(sum);
				System.out.println("&&&&&&&&&&" + sendToken);
							
				SendThread sendingThread = new SendThread(sctpChannel, sendToken.toString());
				Thread t = new Thread(sendingThread);
				t.start();
			} else if(message.contains("#")) {
				
				SumDistributed.done += 1;
				
			} else {
				System.out.println("-------------------------------------------------------------");
//				System.out.println("The final sum is " + message + "Label: " + SumDistributed.label);
				SumDistributed.finalSum = Integer.parseInt(message.trim());
				SumDistributed.done += 1;
				SendBroadcastDone();
			}
			
			
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}

	private static void SendBroadcastDone() {
		// TODO Auto-generated method stub
		
		for(int i=0; i<lineCount; i++) {
			String address = SumDistributed.address.get(i);
			int port = SumDistributed.port.get(i);
			if((nodeId-1) != i) {
				
				try {
					InetSocketAddress socketAddress = new InetSocketAddress(address, port);
					SctpChannel sctpChannel;
					
					sctpChannel = SctpChannel.open();
					sctpChannel.connect(socketAddress);
					
					String msg = "#";
					
					SendThread sendingThread = new SendThread(sctpChannel, msg);
					Thread t = new Thread(sendingThread);
					t.start();
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				

			}
		}
		
		
		
	}
}
