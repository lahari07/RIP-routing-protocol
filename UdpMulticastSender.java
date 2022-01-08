import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

// Code originally from:
//https://www.developer.com/java/data/how-to-multicast-using-java-sockets.html
//
// edited by Sam Fryer.


public class UdpMulticastSender implements Runnable  {

   public int port = 63001; // port to send on
   public String broadcastAddress; // multicast address to send on
   public int node = 0; // the arbitrary node number of this executable
   public RoutingTable ripPacket;

   // standard constructor
   public UdpMulticastSender(int thePort, String broadcastIp, RoutingTable ripPacket )
   {
      port = thePort;
      broadcastAddress = broadcastIp;
      this.ripPacket = ripPacket;
      node = ripPacket.cameraId;
   }
  
   // Send the UDP Multicast message
   public void sendUdpMessage(RIPPacket ripObj) throws IOException {
      // Socket setup
      DatagramSocket socket = new DatagramSocket();
      InetAddress group = InetAddress.getByName(broadcastAddress);
      
      // Packet setup
      byte[] msg = ripObj.getBytes();
      DatagramPacket packet = new DatagramPacket(msg, msg.length, group, port);

      // let 'er rip
      socket.send(packet);
      socket.close();
   }

   // the thread runnable.  Starts sending packets every 500ms.
   @Override
   public void run(){
      while (true)
      {
        try {
          // set our message as "Node 1" (or applicable number)
        	RIPPacket rp = new RIPPacket();
        	rp.generatePacket(node, ripPacket);
          
          sendUdpMessage(rp);
          Thread.sleep(5000);
        }catch(Exception ex){
          ex.printStackTrace();
        }
      }
   }
}
