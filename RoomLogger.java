import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RoomLogger {
    /**
     * บันทึก log และจัดการไฟล์สถานะของห้องในโฟลเดอร์
     * 
     * @param floorFolderName : ชื่อโฟลเดอร์ floor เช่น "floor_1"
     * @param roomNumber      : หมายเลขห้อง (เช่น "1" สำหรับห้อง 101 เมื่ออยู่ชั้น 1)
     * @param userName        : ชื่อผู้ใช้
     * @param action          : Action เช่น "Added", "Moved In", "Moved Out", "Cancelled"
     */
    public static void logRoomEvent(String floorFolderName, String roomNumber, String userName, String action) {
        // สร้างโฟลเดอร์ floor (เช่น floor_1) หากไม่มีอยู่
        File floorDirectory = new File(floorFolderName);
        if (!floorDirectory.exists()) {
            floorDirectory.mkdirs();
        }
        
        // ดึงหมายเลขชั้นออกจากชื่อโฟลเดอร์ (floor_1 => "1")
        String floorNo = floorFolderName.substring("floor_".length());
        int roomNum = Integer.parseInt(roomNumber);
        // ถ้าหมายเลขห้องน้อยกว่า 10 ให้เติม 0 ข้างหน้า (1 => 01)
        String paddedRoom = (roomNum < 10 ? "0" + roomNumber : roomNumber);
        // สร้างชื่อโฟลเดอร์ห้อง เช่น "Room 101" (เมื่อ floorNo = "1" และ roomNumber = "1")
        String roomFolderName = "Room " + floorNo + paddedRoom;
        
        // สร้างโฟลเดอร์สำหรับห้องภายในโฟลเดอร์ floor
        File roomDirectory = new File(floorDirectory, roomFolderName);
        if (!roomDirectory.exists()) {
            roomDirectory.mkdirs();
        }
        
        // กำหนด path สำหรับ log file ภายใน roomDirectory
        File logFile = new File(roomDirectory, "log.txt");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());
        
        // บันทึก log ลงใน log.txt (append mode)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write("User: " + userName + " | " + action + " at: " + currentTime);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving room log info: " + e.getMessage());
        }
        
        // กำหนด path สำหรับ status file ภายใน roomDirectory
        File statusFile = new File(roomDirectory, "status.txt");
        if (action.equals("Added") || action.equals("Moved In")) {
            // สร้าง status file เพื่อบ่งบอกว่าห้องมีผู้ใช้งานอยู่
            if (!statusFile.exists()) {
                try {
                    statusFile.createNewFile();
                } catch (IOException e) {
                    System.err.println("Error creating occupancy status file: " + e.getMessage());
                }
            }
        } else if (action.equals("Moved Out") || action.equals("Cancelled")) {
            // ลบ status file เพื่อบ่งบอกว่าห้องว่างแล้ว
            if (statusFile.exists()) {
                if (!statusFile.delete()) {
                    System.err.println("Error deleting occupancy status file for room: " + roomFolderName);
                }
            }
        }
    }
}
