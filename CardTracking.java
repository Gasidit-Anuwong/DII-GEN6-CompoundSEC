import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CardTracking {
    private static final String LOG_FILE = "access_log.txt";

    public static void logAccess(String cardID, int floor, int room) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write("Card ID: " + cardID + " accessed Floor: " + floor + ", Room: " + room + " at " + timestamp + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to log file.");
        }
    }
}
