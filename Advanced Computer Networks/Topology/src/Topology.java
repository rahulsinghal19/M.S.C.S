/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Rahul
 */
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Maintains the node information Node id, list of ip's in network, degree
 * information of each node & nodes connected in the network
 *
 */
class nodeInfo implements Serializable {

    public nodeInfo() {
        id = 0;
        ip = null;
        n = 0;
        networkInfo = new ArrayList<String>();
        availableServer = new ArrayList<String>();
        degreeInfo = new ArrayList<Integer>();
        degreeInfo.add(0, 0);
        degreeInfo.add(1, 0);
        degreeInfo.add(2, 0);
        degreeInfo.add(3, 0);
        degreeInfo.add(4, 0);
        degreeInfo.add(5, 0);
    }
    int id;
    String ip;
    int port;
    int distance;
    int n;
    private String packetType;

    public String getPacketType() {
        return packetType;
    }

    public void setPacketType(String packetType) {
        this.packetType = packetType;
    }
    ArrayList<String> networkInfo;
    ArrayList<String> availableServer;
    ArrayList<Integer> degreeInfo;
}

/**
 * TCP thread which continuously accepts new connections & listens
 *
 */
class TCPServerThread implements Runnable {

    @Override
    public void run() {
        try {
            ServerSocket server = new ServerSocket(8080);
            System.out.println("Created serversocket");
            while (true) {
                System.out.println("Server accepting connections..");
                Socket p = server.accept();
                Topology.getMyNode().n++;
                System.out.println("Accepted a conenction, creating a nrw Thread for listening");
                new Thread(new receivingTcpServer(p)).start();


                System.out.println("At end of receivingTcpThread");

            }
        } catch (IOException ex) {
            System.err.println("Error at TCPServerThread: " + ex.getMessage());
        }
    }
}

/**
 * Maintains the Topology information by each node in the network
 *
 */
public class Topology {

    private static nodeInfo myNode;

    public static nodeInfo getMyNode() {
        return myNode;
    }

    public static void setMyNode(nodeInfo myNode) {
        Topology.myNode = myNode;
    }
    private static UDPServerThread udpServerJoin;
    private static UDPServerThread udpServerQuery;
    private static TCPServerThread tcpServer;
    private static HashMap<Socket, Object[]> Streams;
    private static HashMap<Integer, Socket> connectedNodes;

    public static HashMap<Integer, Socket> getConnectedNodes() {
        return connectedNodes;
    }

    public static void setConnectedNodes(HashMap<Integer, Socket> connectedNodes) {
        Topology.connectedNodes = connectedNodes;
    }
    private static String[] ipsToConnect;

    public static void setIpsToConnect(String[] ipsToConnect) {
        for (int i = 0; i < ipsToConnect.length; i++) {
            Topology.ipsToConnect[i] = ipsToConnect[i];
        }
    }

    public static String[] getIpsToConnect() {
        return ipsToConnect;
    }
    final private int n = 6; //Number of nodes in network (max. n=6)
    final private int m = 2; //m0 nodes in the network
    private static int distance[][]; // maintains the distance table for each node
    private static int degree[]; //maintains the degree information of each node

    public static int[] getDegree() {
        return degree;
    }

    public static void setDegree(int[] degree) {
        Topology.degree = degree;
    }

    public static int[][] getDistance() {
        return distance;
    }

    public static void setDistance(int[][] distance) {
        Topology.distance = distance;
    }

    public static void setStreams(HashMap<Socket, Object[]> Streams) {
        Topology.Streams = Streams;
    }

    public static HashMap<Socket, Object[]> getStreams() {
        return Streams;
    }

    /**
     * Constructor for the Topology class
     *
     */
    public Topology() {
        System.out.println("Initializing the Topology");
        Streams = new HashMap<Socket, Object[]>();
        connectedNodes = new HashMap<Integer, Socket>();

        distance = new int[n][n];
        for (int i = 0; i < distance.length; i++) {
            for (int j = 0; j < distance.length; j++) {
                distance[i][j] = -1;
            }
        }

        degree = new int[n];
        for (int i = 0; i < degree.length; i++) {
            degree[i] = 0;
        }

        ipsToConnect = new String[m];
        Topology.myNode = new nodeInfo();
    }

    /**
     * This method is invoked by the UDPServerThread If there are only 2 nodes
     * in the network, it returns both the ip addresses If the number of nodes >
     * 2, the ip addresses are returned based on the weighted probability From
     * the degree table, an array is created from which the ip addresses are
     * chosen randomly
     *
     */
    public static String getRandomIP() {
        int sizeofAvailableServer = myNode.availableServer.size();
        String toReturnStringofIPs = null;
        int choice1, choice2;

        int total = 0, k = 0, ip1 = 0, ip2 = 0;

        System.out.println("Size: " + sizeofAvailableServer);
        if (sizeofAvailableServer == 2) {
            System.out.println("The size of the network is 2, hence returning both the ips");
            toReturnStringofIPs = myNode.availableServer.get(0) + " " + myNode.availableServer.get(1);
        } else {
            //code to return the ip's randomly

            /*            System.out.println("There are more than m=2 nodes in the network, sending random ips from availabe servers");
             Random selectIPs = new Random();
             choice1 = selectIPs.nextInt(sizeofAvailableServer);

             while (choice1 == (choice2 = selectIPs.nextInt(sizeofAvailableServer))) {
             }
             toReturnStringofIPs = myNode.availableServer.get(choice1) + " " + myNode.availableServer.get(choice2);
             */

            //code to return the ip addresses based on the weighted probability

            for (int i = 0; i < Topology.getDegree().length; i++) {
                total += Topology.getDegree()[i];
            }
            System.out.println("Total : " + total);
            int weightArray[] = new int[total];

            for (int i = 0; i < weightArray.length; i++) {
                System.out.println("Weight Array print ................ " + weightArray[i]);
            }

            for (int i = 0; i < Topology.getDegree().length; i++) {
                if (Topology.getDegree()[i] != 0) {
                    for (int j = 0; j < Topology.getDegree()[i]; j++) {
                        weightArray[k] = i;
                        System.out.println("Weight Array at : " + k + " " + weightArray[k]);
                        k++;

                    }
                }
            }

            Random selectIPs = new Random();
            choice1 = selectIPs.nextInt(total);
            ip1 = weightArray[choice1];

            while (ip1 == (ip2 = weightArray[selectIPs.nextInt(total)])) {
            }
            System.out.println("ip1: " + ip1 + " ..|.." + "ip2: " + ip2);
            toReturnStringofIPs = myNode.availableServer.get(ip1) + " " + myNode.availableServer.get(ip2);

        }
        System.out.println("Returning IPs");
        return toReturnStringofIPs;
    }

    public static String setNullAddress() {
        return (null + " " + null);
    }

    /**
     * Starts the severs based on the type of node (m0 or new node)
     *
     */
    private static void init(boolean value) {
        //open UDP, TCP sockets and wait for input
        udpServerJoin = new UDPServerThread(10000);
        udpServerQuery = new UDPServerThread(9090);
        tcpServer = new TCPServerThread();

        System.out.println("Topology : init");
        //if a server
        if (value == true) {
            new Thread(udpServerJoin).start();
            new Thread(udpServerQuery).start();
            new Thread(tcpServer).start();
            new Thread(new updateRoutes()).start();
        } else {
            new Thread(udpServerQuery).start();
            new Thread(tcpServer).start();
            new Thread(new updateRoutes()).start();
        }

    }

    /**
     * This method writes a packet from a client to server on the recorded
     * streams
     *
     * @param socket (The sending socket)
     * @param packetType (client_initial or server_initial or update packet)
     * being written
     *
     */
    public static synchronized void writeData(Socket socket, String packetType) throws IOException {
        Object[] StoreDetails = new Object[2];
        if (Topology.Streams.get(socket) == null) {
            System.out.println("Under writeData!!!!!!");
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            StoreDetails[0] = in;
            StoreDetails[1] = out;
            Topology.getStreams().put(socket, StoreDetails);
        }
        ObjectOutputStream out = (ObjectOutputStream) Streams.get(socket)[1];
        myNode.setPacketType(packetType);
        System.out.println("in write data:............ " + myNode.getPacketType());
        out.writeObject(myNode);
        out.reset();
        System.out.println("Written data...." + myNode.getPacketType());
    }

    /**
     * This method updates the routing information for each node in the network
     *
     * @param fromId (the update packed received from) &
     * @param al which represents the network info of the node
     *
     */
    public static synchronized void updateRoutingInfo(int fromId, ArrayList<String> al) {

        for (int i = 0; i < 6; i++) {
            System.out.print(distance[myNode.id - 1][i] + " ");
        }


        String localList[] = new String[3];
        for (int i = 0; i < al.size(); i++) {
            localList = al.get(i).split(" ");
            System.out.println("distance ........... " + distance[myNode.id - 1][Integer.parseInt(localList[0]) - 1]);
            if (Integer.parseInt(localList[0]) == myNode.id) {
                continue;
            }

            if (distance[myNode.id - 1][Integer.parseInt(localList[0]) - 1] == 1) {
                //It is a direct connection
                continue;
            } else if (distance[myNode.id - 1][Integer.parseInt(localList[0]) - 1] == -1) {
                int distToNodeSendingInfo = distance[myNode.id - 1][fromId - 1];
                distance[myNode.id - 1][Integer.parseInt(localList[0]) - 1] = distToNodeSendingInfo + 1;
                myNode.networkInfo.add(localList[0] + " " + fromId + " " + distance[myNode.id - 1][Integer.parseInt(localList[0]) - 1]);
            }
        }
    }

    public static void printRouteInfo() {
        System.out.println("in Printing Route Informtion.");
        for (int index = 0; index < myNode.networkInfo.size(); index++) {
            System.out.println(myNode.networkInfo.get(index));
        }
    }

    /**
     * This method updates the Degree information for each node in the network
     *
     * @param fromNode id of the sender node
     * @param degAl the degree list received updates the degree info array
     *
     */
    public static synchronized void updateDegreeInfo(int fromNode, ArrayList<Integer> degAl) {
        System.out.println("!!!!!!!!Degree in updateDegree!!!!! " + fromNode + " " + degAl.get(fromNode - 1));
        myNode.degreeInfo.set(fromNode - 1, degAl.get(fromNode - 1));
        degree[fromNode - 1] = degAl.get(fromNode - 1);
        Topology.setDegree(degree);

    }

    public static void printDegreeInfo() {
        System.out.println("Printing degree info of each node....");
        for (int id = 0; id < myNode.degreeInfo.size(); id++) {
            System.out.println(myNode.degreeInfo.get(id));
        }
        for (int id = 0; id < degree.length; id++) {
            System.out.println("Degree: " + degree[id]);
        }
    }

    /**
     * This method processes whether is a client_initial or server_initial or
     * update packet updates the network info, available server & degree info
     * list
     *
     * @param socket the socket from whom it has received
     * @param packetInfo specifies the type of packet received
     *
     */
    public static synchronized void processData(Socket socket, nodeInfo packetInfo) throws IOException {
        System.out.println("Topology : processData ");
        System.out.println("in Process data Packet type " + packetInfo.getPacketType());
        switch (packetInfo.getPacketType()) {
            //add ip address of other servers which are received during period update
            case "client_initial":
                String info = String.valueOf(packetInfo.id) + " " + String.valueOf(packetInfo.id) + " " + "1";
                myNode.networkInfo.add(info);
                myNode.availableServer.add(packetInfo.ip);
                distance[myNode.id - 1][packetInfo.id - 1] = 1;
                //sets degree information to node & its degree array
                myNode.degreeInfo.set(myNode.id - 1, myNode.n);
                degree[myNode.id - 1] = myNode.n;
                System.out.println("myNode Available server: " + myNode.availableServer);
                System.out.println("myNode Degree info: " + myNode.degreeInfo);
                connectedNodes.put(packetInfo.id, socket);
                writeData(socket, "server_initial");
                break;
            case "server_initial":
                info = String.valueOf(packetInfo.id) + " " + String.valueOf(packetInfo.id) + " " + "1";
                myNode.networkInfo.add(info);
                distance[myNode.id - 1][packetInfo.id - 1] = 1;
                //sets degree information to node & its degree array
                myNode.degreeInfo.set(myNode.id - 1, myNode.n);
                degree[myNode.id - 1] = myNode.n;
                myNode.availableServer.add(packetInfo.ip);
                connectedNodes.put(packetInfo.id, socket);
                System.out.println("myNode Available server: " + myNode.availableServer);
                System.out.println("myNode Degree info: " + myNode.degreeInfo);
                break;
            case "update":
                updateRoutingInfo(packetInfo.id, packetInfo.networkInfo);
                printRouteInfo();
                updateDegreeInfo(packetInfo.id, packetInfo.degreeInfo);
                printDegreeInfo();
                break;
        }
    }

    @SuppressWarnings("ConvertToTryWithResources")
    public static void main(String[] args) {

        String readIPs[] = new String[2];
        Topology t = new Topology();

        if (args.length == 0) {
            System.out.println("Error: java Topology <routerId>");
            System.exit(1);
        }
        System.out.println(Topology.myNode);
        myNode.id = Integer.parseInt(args[0]);
        try {
            myNode.ip = InetAddress.getLocalHost().getHostAddress();
            myNode.port = 8080;
            myNode.availableServer.add(myNode.ip);
            distance[myNode.id - 1][myNode.id - 1] = 1;
            System.out.println("Recorded the IP, Port; Added the IP to the available list");
        } catch (UnknownHostException ex) {
            Logger.getLogger(Topology.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (args.length == 1) {
//            String fileName = "C:\\Users\\Rahul\\Documents\\NetBeansProjects\\machineALL\\src\\initIP2.txt";
            String fileName = "/home/004/r/rx/rxs132730/degreetest/initIP.txt";
            try {
                //Create object of FileReader
                FileReader inputFile = new FileReader(fileName);

                //Instantiate the BufferedReader Class
                BufferedReader bufferReader = new BufferedReader(inputFile);

                //Variable to hold the one line data
                String line;
                System.out.println("Reading the ip's from the file and comparing..");
                // Read file line by line and print on the console
                while ((line = bufferReader.readLine()) != null) {
                    if (line.equals(InetAddress.getLocalHost().getHostAddress())) {
                        if (myNode.id == 1) {
                            System.out.println("Topology 1 started");
                            readIPs[0] = line;
                            System.out.println(readIPs[0]);
                            init(true);
                            break;
                        } else {
                            System.out.println("Topology 2 started");
                            init(true);
                            Socket client = new Socket(InetAddress.getByName(readIPs[1]), 8080);
                            System.out.println("Topology 2 connected on TCP to the Topology 1");
                            new Thread(new receivingTcpServer(client)).start();
                            Thread.sleep(2000);
                            System.out.println("Thread created for Topology 2");
                            writeData(client, "client_initial");
                            System.out.println("Sent inital packet, Topology 2");
                            myNode.n++;
                            break;
                        }
                    } else {
                        readIPs[1] = line;
                    }
                }

                //Close the buffer reader
                bufferReader.close();
            } catch (Exception e) {
                System.out.println("Error while reading file line by line:"
                        + e.getMessage());
            }

            //for node 3 & up, randomly choose one from an array of otherIP (mo nodes ip address) and connect on UDP
            //got data, runtime connect to the ip's and send initial packet
            // start servers from init based on the bool value;

            if (myNode.id > 2) {
                Random server = new Random();
                int select = server.nextInt(2);
                System.out.println("new Node sending Topology " + (select + 1) + " join request " + readIPs[select]);
                Thread udpThread = new Thread(new UDPClientThread(readIPs[select], 10000));
                udpThread.start();
                try {
                    udpThread.join();
                    System.out.println("udpThread received the ip's after join");

                } catch (InterruptedException ex) {
                    Logger.getLogger(Topology.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (ipsToConnect[0] == null && ipsToConnect[1] == null) {
                    System.out.println("Maximum connections reached in network. Cannot connect to the network");
                    System.exit(1);
                } else {
                    for (int i = 0; i < ipsToConnect.length; i++) {
                        try {
                            Socket clientConnectServer = new Socket(ipsToConnect[i], 8080);
                            System.out.println("new Node connected to " + ipsToConnect[i] + " on TCP");
                            new Thread(new receivingTcpServer(clientConnectServer)).start();
                            Thread.sleep(2000);
                            writeData(clientConnectServer, "client_initial");
                            System.out.println("Sent initial packet");
                            myNode.n++;
                        } catch (UnknownHostException ex) {
                            Logger.getLogger(Topology.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException | InterruptedException ex) {
                            Logger.getLogger(Topology.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    System.out.println("Starting the UDP 9090 & TCP 8080 server");
                    init(false);
                }

            }
        }

    }
}
