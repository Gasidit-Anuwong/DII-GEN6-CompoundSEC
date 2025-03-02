import java.util.Date;
import java.text.SimpleDateFormat;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AccessCard {
    private String userName;
    private String floor;
    private String room;
    private Date issueDate;
    private String facadeId; // multi-facade id ที่เข้ารหัสแบบ time-based

    // Constructor แบบ private (ใช้ Factory ในการสร้าง)
    private AccessCard(String userName, String floor, String room, Date issueDate, String facadeId) {
        this.userName = userName;
        this.floor = floor;
        this.room = room;
        this.issueDate = issueDate;
        this.facadeId = facadeId;
    }

    public String getUserName() { return userName; }
    public String getFloor() { return floor; }
    public String getRoom() { return room; }
    public Date getIssueDate() { return issueDate; }
    public String getFacadeId() { return facadeId; }

    // Factory สำหรับสร้าง AccessCard (Factory Pattern)
    public static class AccessCardFactory {
        public static AccessCard createAccessCard(String userName, String floor, String room) {
            Date now = new Date();
            String facadeId = generateFacadeId(userName, floor, room, now);
            return new AccessCard(userName, floor, room, now, facadeId);
        }

        // Time-based encryption สำหรับสร้าง facadeId
        private static String generateFacadeId(String userName, String floor, String room, Date time) {
            String data = userName + floor + room + new SimpleDateFormat("yyyyMMddHHmmss").format(time);
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hash = md.digest(data.getBytes());
                return Base64.getEncoder().encodeToString(hash);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return data; // fallback
            }
        }
    }
}