
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * UDPServerThread handles all the request received from the new node in the network
 * 
 * @author Rahul
 */
public class UDPServerThread implements Runnable {

    private int port;

    public UDPServerThread(int port) {
        this.port = port;
    }
    DatagramSocket udpServerSocket = null;
    byte[] receiveData = new byte[1024];
    byte[] sendData = new byte[1024];
    String join = "join";
    String query = "query";
    String degree = "degree";
    String farthest = "farthest";
    String returnMsg = "Recieved join, returning IP:port details";
    String rejectMsg = "Reject";

    @Override
    public synchronized void run() {
        try {
            udpServerSocket = new DatagramSocket(port);

            while (true) {
                System.out.println("UDPServerThread receiving packet");
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                udpServerSocket.receive(receivePacket);
                String sentence = new String(receivePacket.getData());
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                //Handles join requests on UDP
                if (join.equalsIgnoreCase(sentence.trim()) && Topology.getMyNode().availableServer.size() < 7) { //add the condition to check maxNumberofConnections (n)
                    System.out.println("Join request at UDPServerThread");
                    returnMsg = Topology.getRandomIP();
                    System.out.println(returnMsg);
                    sendData = returnMsg.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                    udpServerSocket.send(sendPacket);
                    System.out.println("sent the IPs to connect by the UDPServerThread");
                } 
                //handles query request to display the routing table for the node
                else if (query.equalsIgnoreCase(sentence.trim())) {
                    returnMsg = "";
                    for (int index = 0; index < Topology.getMyNode().networkInfo.size(); index++) {
                        returnMsg += Topology.getMyNode().networkInfo.get(index);
                        returnMsg += "\n";
                    }
                    System.out.println(returnMsg);
                    sendData = returnMsg.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                    udpServerSocket.send(sendPacket);
                    System.out.println("sent the IPs to connect by the UDPServerThread");
                } 
                //handles the degree request for any node in the network
                else if (degree.equalsIgnoreCase(sentence.trim())) {
                    String degS = "" + (Topology.getMyNode().n);
                    returnMsg = degS;
                    System.out.println(returnMsg);
                    sendData = returnMsg.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                    udpServerSocket.send(sendPacket);
                    System.out.println("sent the degree information by UDPServerThread");
                } 
                //handles the farthest node information for any node in the network
                else if (farthest.equalsIgnoreCase(sentence.trim())) {
                    int size = Topology.getMyNode().networkInfo.size();
                    returnMsg = "";
                    String[][] parse = new String[size][];
                    int[] allMaxIndices = new int[size];
                    int count = 0;
                    for (int index = 0; index < size; index++) {
                        parse[index] = Topology.getMyNode().networkInfo.get(index).split(" ");
                    }
                    for (int index = 0; index < size; index++) {
                        if(1 != Integer.parseInt(parse[index][2])) {
                            allMaxIndices[count++] = Integer.parseInt(parse[index][0]);
                        }                        
                    }
                    
                    for(int i=0;i<allMaxIndices.length;i++) {
                        if(allMaxIndices[i] != 0) {
                            returnMsg += allMaxIndices[i];
                            returnMsg += " ";
                        }
                    }                    
                    System.out.println(returnMsg);
                    sendData = returnMsg.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                    udpServerSocket.send(sendPacket);
                    System.out.println("sent the node id's of largest hops by the UDPServerThread");
                } 
                //If UDPServer received a request other than the specified one
                else {
                    System.out.println("Recieved other than join request :  Degree limit reached");
                    rejectMsg = Topology.setNullAddress();
                    sendData = rejectMsg.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                    udpServerSocket.send(sendPacket);
                    System.out.println("Sent reject reply");
                }
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
