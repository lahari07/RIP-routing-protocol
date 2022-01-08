import java.net.InetAddress;

public class camera {
	 public static void main(String[] args) {
		 if(args.length > 0) {
			 int cameraId = Integer.parseInt(args[0]);
			 System.out.println("Argumeant 1: " + args[0]);
			 System.out.println("Argumeant 2: " + args[1]);
			 String[] secondArg = args[1].split("\\:");
			 String jungleCloudIp = secondArg[0];
			 int port = Integer.parseInt(secondArg[1]);
			 
			 try
	        	{
				 InetAddress localhost = InetAddress.getLocalHost(); 
				 String address = (localhost.getHostAddress()).trim(); 
				 
				 System.out.println("Broadcasting from: "+address);
				 	RoutingTable r = new RoutingTable(cameraId, address);
				 	TimerToExec t = new TimerToExec(r);
				 	t.start();
				 	
		            Thread unicastClient = new Thread(new UdpUnicastClient(70000, r, t, jungleCloudIp, port));
                    unicastClient.start();
                    Thread unicastServer = new Thread(new UdpUnicastSender(70000, r, t, jungleCloudIp, port));
                    unicastServer.start();
                    
	               
	                
	                System.out.println("Starting Multicast Receiver...");
					Thread client=new Thread(new UdpMulticastClient(63001,"230.230.230.230",r,t));
					client.start();
	                    
	                System.out.println("Starting Multicast Sender...");
					Thread sender=new Thread(new UdpMulticastSender(63001,"230.230.230.230",r));
					sender.start();
					
					
					
					
					
				 
	        	} catch(Exception ex){
	        		System.out.println(ex);
	        	}
			 
			 
		 } else {
			 System.out.println("Specify the command line arguments <camera ID> <Jungle cloud IP:port>");
		 }
	 }
}
