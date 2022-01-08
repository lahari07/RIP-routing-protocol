import java.util.*;

public class RoutingTable {
	String ipAddress;
	List<RouteRow> routeTable = new ArrayList<RouteRow>();
	int cameraId;
	int jungleCloudId;
	String nextHop;
	RIPPacket senderrip = new RIPPacket();
	public RoutingTable(int cameraId, String ipAddress) {
		this.cameraId = cameraId;
		this.ipAddress = ipAddress;
		RouteRow r = new RouteRow(this.cameraId, this.ipAddress, this.ipAddress, 0);
		this.routeTable.add(r);
	}
	
	 public void addNewEntry(byte[] packet, String senderIp) {
		 List<RouteRow> receivedRouteTable = breakPacket(packet,senderIp);
		 printTable();
		 update(receivedRouteTable, this.senderrip);
	 }
	 
	 
	 public List<RouteRow> breakPacket(byte[] RIPPacket, String senderIp) {
			List<RouteRow> returnTable  = new ArrayList<>();
			int id = RIPPacket[2];
			int iterLen = RIPPacket.length - 4;
			int index = 4;
			boolean breakLoop = false;
			
			for(int i=0; i<iterLen; i++) {
				
				index += 2;
				int nodeId = Integer.parseInt(RIPPacket[index & 0xff] +"" +RIPPacket[(index +1)& 0xff]);
				index+=2;
				

				byte camIPb1 = RIPPacket[index++];
				byte camIPb2 = RIPPacket[index++];
				byte camIPb3 = RIPPacket[index++];
				byte camIPb4 = RIPPacket[index++];
				
				String cameraIp = getIPFromBytes(camIPb1, camIPb2, camIPb3, camIPb4);
				index += 4;
				
				byte nextHopb1 = RIPPacket[index++];
				byte nextHopb2 = RIPPacket[index++];
				byte nextHopb3 = RIPPacket[index++];
				byte nextHopb4 = RIPPacket[index++];
				String nextHop = getIPFromBytes(nextHopb1, nextHopb2, nextHopb3, nextHopb4);
				
				byte costb1 = RIPPacket[index++];
				byte costb2 = RIPPacket[index++];
				byte costb3 = RIPPacket[index++];
				byte costb4 = RIPPacket[index++];
				
				int cost = getCostfromBytes(costb1, costb2, costb3, costb4);
				System.out.println("After cost: "+cost);

				RouteRow r = new RouteRow(nodeId, cameraIp,nextHop, cost );
				if(cameraIp.equalsIgnoreCase("0.0.0.0") || nextHop.equalsIgnoreCase("1.1.1.1")) {
					breakLoop = true;
					break;
				}
				
				if(!breakLoop) {
					returnTable.add(r);
				}
				
			}
			RoutingTable rt = new RoutingTable(id,senderIp);
			this.senderrip.generatePacket(2, rt);
			return returnTable;
		}
		
		public String getIPFromBytes(byte b1, byte b2, byte b3, byte b4) {
			String ip = "";
			ip += (b1 & 0xff) + ".";
			ip += (b2 & 0xff) + ".";
			ip += (b3 & 0xff) + ".";
			ip += (b4 & 0xff);
			
			return ip;	
		}
		
		public int getCostfromBytes(byte b1, byte b2, byte b3, byte b4) {
			return Integer.valueOf(b4 &  0xff);
		}

	 public void update(List<RouteRow> recRouteTable,RIPPacket ripPack) {
	
		checkCurrent(ripPack);

	        	for(int i=0; i<recRouteTable.size(); i++) {
	        		RouteRow eachReceivedRow = recRouteTable.get(i);
        			boolean checkRec = false;
	            for ( int j=0; j<this.routeTable.size();j++ ){
	            	RouteRow checking = this.routeTable.get(j);
	                if (checking.cameraIp.equalsIgnoreCase(eachReceivedRow.cameraIp)){
	                	checkRec = true;
	                    if(checking.nextHopIp.equalsIgnoreCase(ripPack.ip)){
	                        int minCost = Math.min(1+eachReceivedRow.cost, 16);
	                        if (checking.cost != minCost ){
	                        	checking.setNextHopIp( ripPack.ip);
	                        	checking.setNodeId(ripPack.cameraId); 
	                        	checking.setCost(minCost);
	                        }
	                        
	                       
	                    }else if (checking.cost > 1+ eachReceivedRow.cost){
	                            	checking.setNextHopIp(ripPack.ip);
		                            checking.setNodeId(ripPack.cameraId);
	                            	checking.setCost(1+ eachReceivedRow.cost);
		                            
	                        }
	                    
	                }
	            }
	            if(!checkRec) {
	            		RouteRow rt = new RouteRow(ripPack.cameraId, eachReceivedRow.cameraIp, ripPack.ip,1+ eachReceivedRow.cost);
		            	this.routeTable.add(rt);
	            	
	            }
        	}
	        
	        printTable();
	    }
	 
	 public void checkCurrent(RIPPacket ripPack) {
		 boolean seenSender = false; 
		 for(int i=0;i<this.routeTable.size(); i++) {
	       RouteRow row = routeTable.get(i);
	            if (row.cameraIp.equalsIgnoreCase(ripPack.ip)){
	            	row.cameraId = ripPack.cameraId;
	            	row.nextHopIp = ripPack.ip;
	            	seenSender = true;
	                row.cost = 1;
	            }
	        
		 }
	        
	        if (!seenSender) {
	            RouteRow rt = new RouteRow(ripPack.cameraId, ripPack.ip, ripPack.ip,1);
	            this.routeTable.add(rt);
	        }
	 }
	 
	
	  public void printTable(){
		  String tab = "\t\t\t\t\t";
	        System.out.println("Address"+tab+  "Next Hop"+tab+  "Cost");
	        System.out.println("======================================================================");
	        for (RouteRow rt : this.routeTable){
	        	String content = rt.cameraIp+ tab +rt.nextHopIp+"("+rt.cameraId+")"+ tab +rt.cost;
	            System.out.println(content);
	        }
	    }
	 
	public List<RouteRow> getRoutingTable(){
		return this.routeTable;
	}
	
	public int getCameraId() {
		return this.cameraId;
	}
	
	public String getIp() {
		return this.ipAddress;
	}
}
