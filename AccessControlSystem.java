import java.util.HashMap;

public class AccessControlSystem {
    private HashMap<String, AccessCard> cardDatabase = new HashMap<>();

    public void registerCard(String cardID, AccessCard card) {
        cardDatabase.put(cardID, card);
    }

    public boolean checkAccess(String cardID, int requestedFloor, int requestedRoom) {
        if (!cardDatabase.containsKey(cardID)) {
            return false;
        }
        AccessCard card = cardDatabase.get(cardID);
        return card.getFloor() == requestedFloor && card.getRoom() == requestedRoom;
    }
}
