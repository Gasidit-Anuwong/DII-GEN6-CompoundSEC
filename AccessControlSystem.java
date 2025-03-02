// AccessControlSystem.java
import java.util.ArrayList;
import java.util.List;

// Singleton และ Model สำหรับจัดการการเข้าถึง
public class AccessControlSystem {
    private List<AccessCard> accessCards = new ArrayList<>();

    // Singleton Instance
    private static AccessControlSystem instance;

    private AccessControlSystem() {}

    public static synchronized AccessControlSystem getInstance() {
        if (instance == null) {
            instance = new AccessControlSystem();
        }
        return instance;
    }

    public void addCard(AccessCard card) {
        accessCards.add(card);
    }

    // ตรวจสอบการเข้าถึงโดยอิงจาก userName และ floor
    public boolean verifyAccess(String userName, String floor) {
        for (AccessCard card : accessCards) {
            if (card.getUserName().equals(userName) && card.getFloor().equals(floor)) {
                return true;
            }
        }
        return false;
    }
}
