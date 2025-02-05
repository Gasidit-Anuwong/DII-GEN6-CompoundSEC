import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RoomLogger {
    public static void logRoomActivity(String roomKey, String activity) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStamp = sdf.format(new Date());

        try {
            File file = new File("room_" + roomKey + ".txt");
            FileWriter writer = new FileWriter(file, true); // Append mode
            writer.write(activity + " at " + timeStamp + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
