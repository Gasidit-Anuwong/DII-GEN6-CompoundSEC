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

    // สามารถเพิ่มเมธอดเพิ่มเติมสำหรับค้นหาและจัดการ log ได้ตามต้องการ
}