// AccessCard.java
import java.util.Date;

public class AccessCard {
    private String userName;
    private String floor;
    private String room;
    private Date issueDate;
    
    public AccessCard(String userName, String floor, String room) {
        this.userName = userName;
        this.floor = floor;
        this.room = room;
        this.issueDate = new Date();
    }
    
    public String getUserName() { return userName; }
    public String getFloor() { return floor; }
    public String getRoom() { return room; }
    public Date getIssueDate() { return issueDate; }
}
