
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * updateRoutes Thread sends an update message to each node connected in the network at time interval of 3 seconds
 * 
 * @author Rahul
 */
public class updateRoutes implements Runnable {
    
    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(3000);
                for(int i : Topology.getConnectedNodes().keySet()) {
                    Topology.writeData(Topology.getConnectedNodes().get(i), "update");
                }
            } catch (    InterruptedException | IOException ex) {
                Logger.getLogger(updateRoutes.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
}
