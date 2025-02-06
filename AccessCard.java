public class AccessCard {
    private final String ownerName;
    private final int floor;
    private final int room;

    public AccessCard(String ownerName, int floor, int room) {
        this.ownerName = ownerName;
        this.floor = floor;
        this.room = room;
    }

    public String getOwnerName() {
        return ownerName;
    }



    public int setFloor(){
        return room;
    }
    public int getFloor() {
        return floor;
    }



    public int setRoom(){
        return room;
    }

    public int getRoom() {
        return room;
    }

    public String getDetails() {
        return "Owner: " + ownerName + ", Floor: " + floor + ", Room: " + room;
    }
}
