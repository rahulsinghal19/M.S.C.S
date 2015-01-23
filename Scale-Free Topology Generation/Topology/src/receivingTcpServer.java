
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * receivingTcpServer Thread is invoked by the TCPServerThread & The thread
 * continuously listens for the data.
 *
 */
public class receivingTcpServer extends Thread {

    Socket socketId;
    Object[] StoreDetails = new Object[2];

    public receivingTcpServer(Socket listen) {
        // TODO Auto-generated constructor stub	
        this.socketId = listen;

    }

    public void run() {

        try {
            ObjectOutputStream out = new ObjectOutputStream(socketId.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socketId.getInputStream());
            nodeInfo localNodeInfo;
            StoreDetails[0] = in;
            StoreDetails[1] = out;
            Topology.getStreams().put(socketId, StoreDetails);
            System.out.println("Streams added to the topology's Hash");

            while (true) {
                System.out.println("Object streams created at the receievingTCPServer");
                Object[] toRead = Topology.getStreams().get(socketId);
                ObjectInputStream Read = (ObjectInputStream) toRead[0];
                localNodeInfo = (nodeInfo) Read.readObject();
                System.out.println("       !!!!!!!!      " + localNodeInfo.getPacketType());
                Topology.processData(socketId, localNodeInfo);
                System.out.println("Called Topology : processData");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
