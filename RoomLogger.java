import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RoomLogger {
    /**
     * บันทึก log เหมือนเดิมในไฟล์ roomName.txt
     * และอัปเดตสถานะของห้องในไฟล์ roomName_status.txt:
     * - เมื่อ action เป็น "Added" หรือ "Moved In" จะสร้างไฟล์สถานะขึ้นมา
     * - เมื่อ action เป็น "Moved Out" หรือ "Cancelled" จะลบไฟล์สถานะออก (ถ้ามี)
     */
    public static void logRoomEvent(String folder, String roomName, String userName, String action) {
        // บันทึก log ลงในไฟล์ roomName.txt (log ยังคงถูกเก็บไว้ตลอด)
        File directory = new File(folder);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File logFile = new File(directory, roomName + ".txt");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write("User: " + userName + " | " + action + " at: " + currentTime);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving room log info: " + e.getMessage());
        }
        
        // จัดการไฟล์สถานะ (status file) สำหรับห้องนั้น
        File statusFile = new File(directory, roomName + "_status.txt");
        if (action.equals("Added") || action.equals("Moved In")) {
            // สร้างไฟล์สถานะเพื่อระบุว่าห้องนั้นถูกใช้งานอยู่
            if (!statusFile.exists()) {
                try {
                    statusFile.createNewFile();
                } catch (IOException e) {
                    System.err.println("Error creating occupancy status file: " + e.getMessage());
                }
            }
        } else if (action.equals("Moved Out") || action.equals("Cancelled")) {
            // ลบไฟล์สถานะออกเพื่อระบุว่าห้องนั้นว่างแล้ว
            if (statusFile.exists()) {
                if (!statusFile.delete()) {
                    System.err.println("Error deleting occupancy status file for room: " + roomName);
                }
            }
        }
    }
}
