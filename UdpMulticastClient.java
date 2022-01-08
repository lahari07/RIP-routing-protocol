import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

// Code originally from:
//https://www.developer.com/java/data/how-to-multicast-using-java-sockets.html
//
// edited by Sam Fryer.

public class UdpMulticastClient implements Runnable {

   public int port = 63001; // port to listen on
   public String broadcastAddress; // multicast address to listen on
   
   RoutingTable r;
   TimerToExec t;

   // standard constructor
   public UdpMulticastClient(int thePort, String broadcastIp, RoutingTable r, TimerToExec t)
   {
      port = thePort;
      broadcastAddress = broadcastIp;
      this.r = r;
      this.t = t;
   }

   // listens to the ipaddress and reports when a message arrived
   public void receiveUDPMessage() throws
         IOException {
      byte[] buffer=new byte[1024];

      // create and initialize the socket
      MulticastSocket socket=new MulticastSocket(port);
      InetAddress group=InetAddress.getByName(broadcastAddress);
      socket.joinGroup(group);

      
      while(true){
        try {
          DatagramPacket packet=new DatagramPacket(buffer,buffer.length);

	  // blocking call.... waits for next packet
          socket.receive(packet);
          String msg=new String(packet.getData(),packet.getOffset(),packet.getLength());
          
          String currentAddress = packet.getAddress().toString().substring(1);
          if(currentAddress.equals(r.ipAddress)){
        	  continue;
          }
         

          System.out.println("Reached update Time");
          t.addTimeStamp(currentAddress);
          System.out.println("Reached add entry ");
          r.addNewEntry(packet.getData(), currentAddress);
          System.out.println("Completed add entry");


          if("EXIT".equals(msg)) {
            System.out.println("No more messages. Exiting : "+msg);
            break;
          }
        }catch(IOException ex){
          ex.printStackTrace();
        }
      }

      //close up ship
      socket.leaveGroup(group);
      socket.close();
   }

   // the thread runnable.  just starts listening.
   @Override
   public void run(){
     try {
       receiveUDPMessage();
     }catch(IOException ex){
       ex.printStackTrace();
     }
   }
}
