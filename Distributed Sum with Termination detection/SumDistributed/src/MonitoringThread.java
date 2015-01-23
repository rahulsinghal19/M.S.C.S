
public class MonitoringThread implements Runnable {
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while(SumDistributed.done != 4) {
//			System.out.println(SumDistributed.done);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(SumDistributed.done == 4) {
			//Set the variables to false and quit
			SctpServerThread.serverRun = false;
			System.out.println("-------------------------------------------------------------");
			System.out.println("Label: " + SumDistributed.label + " Final sum: " + SumDistributed.finalSum);
			System.out.println("Done---------------------------------------------------------");
			System.exit(0);
		}

	}

}
