import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class UdpUnicastClient implements Runnable{

    public int port = 63001; // port to listen on
    public RoutingTable routeTable;
    TimerToExec timeoutManager;
    String cloudIp;
    int cloudPort;
    public UdpUnicastClient(int port, RoutingTable cameraTable, TimerToExec timeoutManager, String cloudIp, int cloudPort) {
        this.port = port;
        this.routeTable = cameraTable;
        this.timeoutManager = timeoutManager;
        this.cloudIp = cloudIp;
        this.cloudPort = cloudPort;
    }

    public void receiveUDPMessage() throws
            IOException {
        byte[] buffer = new byte[1024];

        // create and initialize the socket
        DatagramSocket socket = new DatagramSocket(port);
//        InetAddress unicast = InetAddress.getByName(cloudIp);

        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                System.out.println("Waiting to recieve");
                // blocking call.... waits for next packet
                socket.receive(packet);
                String msg = new String(packet.getData(), packet.getOffset(), packet.getLength());
                System.out.println("[iiiiiiii UDP message received from "+packet.getAddress()+"] "+msg);
                // this is updated
                String currentAddress = packet.getAddress().toString().substring(1);
                if(currentAddress.equals(cloudIp)){
                    System.out.println("[iiiiiiii nnn UDP message received from "+currentAddress+"] "+msg);

                    timeoutManager.addTimeStamp(cloudIp);
                    RIPPacket rip = new RIPPacket();
                    rip.generatePacket(2, routeTable);
                    routeTable.addNewEntry(packet.getData(), currentAddress);
                }

                if ("EXIT".equals(msg)) {
                    System.out.println("No more messages. Exiting : " + msg);
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //close up ship
        socket.close();
    }

    @Override
    public void run() {
        try {
            receiveUDPMessage();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
