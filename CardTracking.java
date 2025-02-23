// CardTracking.java
import java.util.ArrayList;
import java.util.List;

public class CardTracking {
    private List<String> trackingLogs = new ArrayList<>();
    
    public void addLog(String log) {
        trackingLogs.add(log);
    }
    
    public List<String> getLogs() {
        return trackingLogs;
    }
    
    // เมธอดเพิ่มเติมสำหรับค้นหาและจัดการ log สามารถเพิ่มได้ตามต้องการ
}
