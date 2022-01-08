import java.util.*;

public class RIPPacket {
	
	int command;
	int version;
	int cameraId;
	String ip;
	int zeros;
	List<RouteRow> routingTable;

	
	public void generatePacket(int command, RoutingTable routeObj ) {
		this.command = command;
		this.version = 2;
		this.cameraId = routeObj.getCameraId();
		this.ip = routeObj.getIp();
		this.zeros = 0;
		this.routingTable = routeObj.getRoutingTable();
	}
	
	public byte[] getBytes() {
		int routingTableSize = this.routingTable.size();
		int bytePacketLength = 20*routingTableSize + 4;
		byte[] ripBytes = new byte[bytePacketLength];
		ripBytes[0] = (byte)this.command;
		ripBytes[1] = (byte)this.version;
		ripBytes[2] = (byte)this.cameraId;
		ripBytes[3] = (byte)this.zeros;
		int index = 4;
		for(int i=0; i<routingTable.size(); i++) {
			
			index = fillAddressIdentifier(ripBytes, index);
			
			index = setRouteTag(ripBytes, index, routingTable.get(i));

			index = storeIpBytes(ripBytes, index, routingTable.get(i).cameraIp);

			index = storeIpBytes(ripBytes, index, "1.1.1.1");

			index = storeIpBytes(ripBytes, index, routingTable.get(i).nextHopIp);

			index = storeCost(ripBytes, index, routingTable.get(i).cost);
			
		}
//		System.out.println("Sent length: "+ ripBytes.length);
		return ripBytes;
	}
	
	public int fillAddressIdentifier(byte[] ripBytes, int index) {
		int i = index;
		ripBytes[i] = (byte) 0;
		i += 1;
		ripBytes[i] = (byte) 0;
		
		return i+1;
	}
	
	public int setRouteTag(byte[] ripBytes, int index, RouteRow obj) {
		int i = index;
		ripBytes[i] = (byte) 0;
		i += 1;
		ripBytes[i] = (byte) obj.cameraId;
		return i+1;
	}
	
	public int storeIpBytes(byte[] ripBytes, int index, String ip) {
		int i = index;
		String[] ipArray = ip.split("\\.");
		for(String ipPart: ipArray) {
			ripBytes[i] = (byte)Integer.parseInt(ipPart);
			i += 1;
		}
		return i;
	}
	
	public int storeCost(byte[] ripBytes, int index, int cost) {
		int i = index;
		ripBytes[i] = 0;
		i += 1;
		ripBytes[i] = 0;
		i += 1;
		ripBytes[i] = 0;
		i+=1;
		ripBytes[i] = (byte)cost;
		return i+1;
	}
	
}
