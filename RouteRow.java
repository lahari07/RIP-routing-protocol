import java.util.*;

public class RouteRow {
	String cameraIp;
	int cameraId;
	int cost;
	String nextHopIp;
	
	RouteRow(int cameraId, String cameraIp, String nextHopIp, int cost){
		this.cameraId = cameraId;
		this.cameraIp = cameraIp;
		this.nextHopIp = nextHopIp;
		this.cost = cost;
	}

	public String getNodeIp() {
		return cameraIp;
	}

	public void setNodeIp(String nodeIp) {
		this.cameraIp = nodeIp;
	}

	public int getCameraId() {
		return this.cameraId;
	}

	public void setNodeId(int nodeId) {
		this.cameraId = nodeId;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public String getNextHopIp() {
		return nextHopIp;
	}

	public void setNextHopIp(String nextHopIp) {
		this.nextHopIp = nextHopIp;
	}
	
}
