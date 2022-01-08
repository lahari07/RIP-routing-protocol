import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TimerToExec extends Thread {
	RoutingTable router;
    ConcurrentHashMap<String, Long> expiryCheck = new ConcurrentHashMap<>();
    

    public TimerToExec(RoutingTable r) {
        this.router = r;
    }
    public void addTimeStamp(String ip) {
        long sysTime = getTime();
        expiryCheck.put(ip, sysTime);
    }
    
    public long getTime() {
    	return System.currentTimeMillis()/1000;
    }
    
    public void removeExpiredEntries(Map.Entry<String, Long> eachEntry) {
   	 for (int i=0; i<router.routeTable.size();i++) {
   		 RouteRow row = router.routeTable.get(i);
            if (row.nextHopIp.equalsIgnoreCase(eachEntry.getKey())) {
            	row.cost = 16;
                this.expiryCheck.remove(eachEntry.getKey());
            }
        }
   }
    @Override
    public void run() {
        while (true) {
            try {
                for (Map.Entry<String, Long> entry : expiryCheck.entrySet()) {
                    long sysTime = getTime();
                    if (sysTime - entry.getValue() > 10) {
                    	removeExpiredEntries(entry);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

  
  
}
