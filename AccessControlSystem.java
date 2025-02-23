// AccessControlSystem.java
import java.util.ArrayList;
import java.util.List;

public class AccessControlSystem {
    private List<AccessCard> accessCards = new ArrayList<>();
    
    public void addCard(AccessCard card) {
        accessCards.add(card);
    }
    
    public boolean verifyAccess(String userName, String floor) {
        for (AccessCard card : accessCards) {
            if (card.getUserName().equals(userName) && card.getFloor().equals(floor)) {
                return true;
            }
        }
        return false;
    }
}
