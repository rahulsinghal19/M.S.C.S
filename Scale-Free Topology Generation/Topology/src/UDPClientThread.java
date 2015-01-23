
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * UDPClientThread to send a join request for every new node which tries to join the network
 * Once receives acceptance, it sets the ips to connect in the Topology
 * 
 * @author Rahul
 */
public class UDPClientThread implements Runnable {

    DatagramSocket udpClientSocket = null;
    String connectToUdpServer;
    int port;
    String joinMessage = "join";
    byte[] sendData = new byte[1024];
    byte[] receiveData = new byte[1024];

    public UDPClientThread(String connectToUdpServer, int port) {
        this.connectToUdpServer = connectToUdpServer;
        this.port = port;
    }

    public void processUDPRequest(String message) {
        try {
            System.out.println("Inside UDPClientThread : processUDPRequest");
            udpClientSocket = new DatagramSocket();
            //Converts the string to an IP address 
            InetAddress IPAddress = InetAddress.getByName(connectToUdpServer);
            sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            udpClientSocket.send(sendPacket);
            System.out.println("Sent join message");
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            udpClientSocket.receive(receivePacket);
            String recvdIPs = new String(receivePacket.getData());
            System.out.println("Received the ip's at UDPClientThread");
            System.out.println("Received new : " + recvdIPs);
            String[] ipsToSend = recvdIPs.split(" ");
            Topology.setIpsToConnect(ipsToSend);
            System.out.println("The Topology's ip's to connect updated");
            udpClientSocket.close();
        } catch (SocketException ex) {
            Logger.getLogger(UDPClientThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(UDPClientThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UDPClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        if (port == 10000) {
            //send join request to the udp server
            System.out.println("Join Request");
            processUDPRequest(joinMessage);
        } else {
            System.out.println("Invalid port enetered -UDP Client Thread");
        }
    }
}
