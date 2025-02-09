import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;

public class CardManagementGui extends JFrame {
    private final JButton addRoomButton;
    private final JComboBox<String> roomSelector; // ใช้ JComboBox สำหรับเลือกห้อง
    private final JTextField userNameField;
    private final JComboBox<String> floorSelector;
    private final Map<String, Set<String>> occupiedRooms; // เก็บข้อมูลห้องที่ถูกใช้แล้ว

    public CardManagementGui() {
        setTitle("Card Management");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        occupiedRooms = new HashMap<>(); // ใช้เก็บข้อมูลห้องที่ถูกใช้แล้ว
        loadOccupiedRooms(); // โหลดข้อมูลห้องที่ถูกใช้แล้ว

        JPanel panel = new JPanel();
        userNameField = new JTextField(10);
        addRoomButton = new JButton("Add Room");

        // Dropdown สำหรับเลือกชั้น (1-8)
        String[] floors = {"1", "2", "3", "4", "5", "6", "7", "8"};
        floorSelector = new JComboBox<>(floors);

        // Dropdown สำหรับเลือกห้อง (จะอัปเดตตามชั้นที่เลือก)
        roomSelector = new JComboBox<>();

        panel.add(new JLabel("Select Floor:"));
        panel.add(floorSelector);
        panel.add(new JLabel("Select Room:"));
        panel.add(roomSelector);
        panel.add(new JLabel("User Name:"));
        panel.add(userNameField);
        panel.add(addRoomButton);

        add(panel);

        // เมื่อเปลี่ยนชั้น ให้โหลดห้องที่ยังว่างอยู่
        floorSelector.addActionListener(e -> updateAvailableRooms());

        addRoomButton.addActionListener(e -> addRoom());

        // โหลดห้องที่ว่างสำหรับชั้นเริ่มต้น
        updateAvailableRooms();
    }

    // โหลดข้อมูลห้องที่ถูกใช้งานจากไฟล์
    private void loadOccupiedRooms() {
        for (int i = 1; i <= 8; i++) { // วนลูปทุกชั้น
            String floorFolder = "floor_" + i;
            File directory = new File(floorFolder);
            Set<String> rooms = new HashSet<>();

            if (directory.exists() && directory.isDirectory()) {
                for (File file : Objects.requireNonNull(directory.listFiles())) {
                    if (file.isFile() && file.getName().endsWith(".txt")) {
                        rooms.add(file.getName().replace(".txt", ""));
                    }
                }
            }
            occupiedRooms.put(floorFolder, rooms);
        }
    }

    // อัปเดตรายการห้องที่ยังว่างอยู่
    private void updateAvailableRooms() {
        String selectedFloor = "floor_" + floorSelector.getSelectedItem().toString();
        roomSelector.removeAllItems();

        Set<String> occupied = occupiedRooms.getOrDefault(selectedFloor, new HashSet<>());
        for (int i = 1; i <= 20; i++) {
            String roomNumber = String.valueOf(i);
            if (!occupied.contains(roomNumber)) {
                roomSelector.addItem(roomNumber); // เพิ่มเฉพาะห้องที่ยังว่างอยู่
            }
        }
    }

    private void addRoom() {
        if (roomSelector.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "No available rooms on this floor.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String roomName = roomSelector.getSelectedItem().toString();
        String userName = userNameField.getText().trim();
        String selectedFloor = "floor_" + floorSelector.getSelectedItem().toString();

        if (userName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a user name", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File directory = new File(selectedFloor);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        saveRoomInfoToFile(selectedFloor, roomName, userName);

        // อัปเดตว่า ห้องนี้ถูกใช้แล้ว
        occupiedRooms.computeIfAbsent(selectedFloor, k -> new HashSet<>()).add(roomName);
        updateAvailableRooms(); // รีเฟรชรายชื่อห้อง

        JOptionPane.showMessageDialog(this, "Room added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveRoomInfoToFile(String folder, String roomName, String userName) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());

        File file = new File(folder, roomName + ".txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write("User: " + userName + " | Accessed at: " + currentTime);
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving room info", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CardManagementGui gui = new CardManagementGui();
            gui.setVisible(true);
        });
    }
}
