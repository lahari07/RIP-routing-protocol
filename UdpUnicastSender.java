import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class UdpUnicastSender implements Runnable{

    public int port = 63001; // port to listen on
    public RoutingTable routeTable;
    TimerToExec timeoutManager;
    String cloudIp;
    int cloudPort;
    public UdpUnicastSender(int port, RoutingTable cameraTable, TimerToExec timeoutManager, String cloudIp, int cloudPort) {
        this.port = port;
        this.routeTable = cameraTable;
        this.timeoutManager = timeoutManager;
        this.cloudIp = cloudIp;
        this.cloudPort = cloudPort;
    }

    public void sendUdpMessage() throws IOException {
        // Socket setup

        DatagramSocket socket = new DatagramSocket();
        InetAddress cloud = InetAddress.getByName(cloudIp);

        // Packet setup
        byte[] msg = new byte[0];
        DatagramPacket packet = new DatagramPacket(msg, msg.length, cloud, cloudPort);
        // let 'er rip
        socket.send(packet);
        System.out.println("message sent from unicast");
        socket.close();
    }

    @Override
    public void run() {
        boolean ss = true;
        while (ss)
        {
            try {
                sendUdpMessage();
                Thread.sleep(5000);
                ss = true;
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
