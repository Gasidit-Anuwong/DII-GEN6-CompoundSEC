// RoomLogger.java
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RoomLogger {
    public static void logRoomEvent(String folder, String roomName, String userName, String action) {
        File directory = new File(folder);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory, roomName + ".txt");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write("User: " + userName + " | " + action + " at: " + currentTime);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving room info: " + e.getMessage());
        }
    }
}
