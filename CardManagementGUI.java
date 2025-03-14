import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class CardManagementGui extends JFrame {
    // UI Components
    private final JComboBox<String> floorSelector;
    private final JComboBox<String> roomSelector;
    private final DefaultComboBoxModel<String> roomModel; // Model สำหรับ combo box
    private final JTextField userNameField;
    private final JPasswordField authCodeField;
    private final JButton addRoomButton;
    private final JButton moveRoomButton;
    private final JButton cancelRoomButton;
    private final JButton checkAccessButton; // ปุ่มตรวจสอบการเข้าถึง

    // Map สำหรับเก็บข้อมูลห้องที่ถูกจองแล้ว
    // Key = floor_#, Value = Set ของ room numbers (ที่ถูกจองอยู่)
    private final java.util.Map<String, Set<String>> occupiedRooms;
    private static final String AUTH_CODE = "admin123";

    // Controller (ส่วนของ Controller ใน MVC)
    private final CardController controller;

    public CardManagementGui() {
        setTitle("Card Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        occupiedRooms = new java.util.HashMap<>();
        loadOccupiedRooms();  // โหลดสถานะห้องจากโฟลเดอร์

        // สร้าง UI Components
        userNameField = new JTextField(15);
        authCodeField = new JPasswordField(15);
        addRoomButton = new JButton("Add Room");
        moveRoomButton = new JButton("Move Room");
        cancelRoomButton = new JButton("Cancel Room");
        checkAccessButton = new JButton("Check Access");

        String[] floors = {"1", "2", "3", "4", "5", "6", "7", "8"};
        floorSelector = new JComboBox<>(floors);
        roomModel = new DefaultComboBoxModel<>();
        roomSelector = new JComboBox<>(roomModel);

        // สร้าง BackgroundPanel สำหรับวาด Background Image
        BackgroundPanel backgroundPanel = new BackgroundPanel("background.jpg");
        backgroundPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // วาง Layout ลงใน backgroundPanel
        gbc.gridx = 0;
        gbc.gridy = 0;
        backgroundPanel.add(new JLabel("Select Floor:"), gbc);
        gbc.gridx = 1;
        backgroundPanel.add(floorSelector, gbc);
        gbc.gridx = 2;
        backgroundPanel.add(new JLabel("Select Room:"), gbc);
        gbc.gridx = 3;
        backgroundPanel.add(roomSelector, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        backgroundPanel.add(new JLabel("User Name:"), gbc);
        gbc.gridy = 2;
        backgroundPanel.add(userNameField, gbc);

        gbc.gridy = 3;
        backgroundPanel.add(new JLabel("Auth Code:[ADMIN ONLY]"), gbc);
        gbc.gridy = 4;
        backgroundPanel.add(authCodeField, gbc);

        gbc.gridy = 5;
        backgroundPanel.add(addRoomButton, gbc);
        gbc.gridy = 6;
        backgroundPanel.add(moveRoomButton, gbc);
        gbc.gridy = 7;
        backgroundPanel.add(cancelRoomButton, gbc);
        gbc.gridy = 8;
        backgroundPanel.add(checkAccessButton, gbc);

        add(backgroundPanel);

        // สร้าง Controller
        controller = new CardController();

        // Event Listeners
        floorSelector.addActionListener(e -> updateAvailableRooms());
        addRoomButton.addActionListener(e -> controller.addRoom());
        moveRoomButton.addActionListener(e -> controller.moveRoom());
        cancelRoomButton.addActionListener(e -> controller.cancelRoom());
        checkAccessButton.addActionListener(e -> controller.showAccessCheckWindow());

        updateAvailableRooms();
    }

    /**
     * โหลดข้อมูลห้องที่ถูกจองแล้ว โดยอ่านจากไฟล์สถานะในแต่ละห้อง
     * โครงสร้าง: floor_# / Room XYZ / status.txt
     * สมมติว่าชื่อห้องใน GUI เก็บเฉพาะหมายเลขห้อง (เช่น "1" สำหรับ Room 101 เมื่ออยู่ชั้น 1)
     */
    private void loadOccupiedRooms() {
        for (int i = 1; i <= 8; i++) {
            String floorFolder = "floor_" + i;
            File floorDirectory = new File(floorFolder);
            Set<String> occupiedRoomsSet = new HashSet<>();
            if (floorDirectory.exists() && floorDirectory.isDirectory()) {
                // ตรวจสอบโฟลเดอร์ย่อย (ห้อง)
                File[] roomDirs = floorDirectory.listFiles(File::isDirectory);
                if (roomDirs != null) {
                    for (File roomDir : roomDirs) {
                        // ตรวจสอบว่าในโฟลเดอร์ห้องมี status.txt หรือไม่
                        File statusFile = new File(roomDir, "status.txt");
                        if (statusFile.exists()) {
                            // สมมติว่า roomDir มีชื่อในรูปแบบ "Room 101"
                            String roomFolderName = roomDir.getName(); // เช่น "Room 101"
                            if (roomFolderName.startsWith("Room ")) {
                                String numberPart = roomFolderName.substring(5); // "101"
                                try {
                                    int fullRoomNumber = Integer.parseInt(numberPart);
                                    // สมมติว่า fullRoomNumber มีรูปแบบ XYZ โดย X คือชั้น และ YZ คือหมายเลขห้อง
                                    int roomNum = fullRoomNumber % 100; // 101 % 100 = 1
                                    occupiedRoomsSet.add(String.valueOf(roomNum));
                                } catch (NumberFormatException e) {
                                    // ข้ามหากไม่ใช่ตัวเลข
                                }
                            }
                        }
                    }
                }
            }
            occupiedRooms.put(floorFolder, occupiedRoomsSet);
        }
    }

    // อัปเดตรายการห้องที่ว่างโดยใช้ array model
    private void updateAvailableRooms() {
        roomModel.removeAllElements();
        String selectedFloor = "floor_" + floorSelector.getSelectedItem().toString();
        Set<String> occupied = occupiedRooms.getOrDefault(selectedFloor, new HashSet<>());
        List<String> availableRooms = new ArrayList<>();
        // สมมติว่าห้องมีหมายเลข 1 ถึง 20 (หมายเลขห้องที่ใช้ใน GUI จะเป็นส่วนท้ายของ Room XYZ)
        for (int i = 1; i <= 20; i++) {
            String roomNumber = String.valueOf(i);
            if (!occupied.contains(roomNumber)) {
                availableRooms.add(roomNumber);
            }
        }
        // อัปเดตรายการใน roomModel
        for (String room : availableRooms) {
            roomModel.addElement(room);
        }
    }

    // Method ที่ใช้โดย controller ในการอัปเดตข้อมูลหลังจากเปลี่ยนแปลงสถานะห้อง
    private void refreshRooms(String floorKey) {
        if (floorKey.equals("floor_" + floorSelector.getSelectedItem().toString())) {
            updateAvailableRooms();
        }
    }

    // ========================= Controller (MVC Controller) =========================
    private class CardController {
        // จองห้องใหม่
        public void addRoom() {
            if (roomModel.getSize() == 0) {
                JOptionPane.showMessageDialog(CardManagementGui.this, "No available rooms on this floor.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String roomNumber = (String) roomSelector.getSelectedItem();
            String userName = userNameField.getText().trim();
            String selectedFloor = "floor_" + floorSelector.getSelectedItem().toString();

            if (userName.isEmpty()) {
                JOptionPane.showMessageDialog(CardManagementGui.this, "Please enter a user name", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // เรียกใช้ RoomLogger เพื่อบันทึก log และสร้างไฟล์สถานะในโฟลเดอร์ที่เหมาะสม
            RoomLogger.logRoomEvent(selectedFloor, roomNumber, userName, "Added");
            occupiedRooms.computeIfAbsent(selectedFloor, k -> new HashSet<>()).add(roomNumber);
            refreshRooms(selectedFloor);
            JOptionPane.showMessageDialog(CardManagementGui.this,
                    "Room added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }

        // ย้ายห้อง - เฉพาะ Admin เท่านั้น (userName ต้องเป็น "Admin" และ authCode = "admin123")
        public void moveRoom() {
            String userName = userNameField.getText().trim();
            if (!userName.equals("Admin")) {
                JOptionPane.showMessageDialog(CardManagementGui.this,
                        "Only Admin can perform room move.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String enteredCode = new String(authCodeField.getPassword());
            if (!enteredCode.equals(AUTH_CODE)) {
                JOptionPane.showMessageDialog(CardManagementGui.this, "Invalid Auth Code!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String currentFloorInput = JOptionPane.showInputDialog(CardManagementGui.this, "Enter current floor number:");
            String currentRoomInput = JOptionPane.showInputDialog(CardManagementGui.this, "Enter current room number:");
            if (currentFloorInput == null || currentRoomInput == null ||
                currentFloorInput.trim().isEmpty() || currentRoomInput.trim().isEmpty()) {
                JOptionPane.showMessageDialog(CardManagementGui.this, "Current floor and room are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String oldFloorKey = "floor_" + currentFloorInput.trim();
            String oldRoom = currentRoomInput.trim();
            Set<String> oldOccupied = occupiedRooms.getOrDefault(oldFloorKey, new HashSet<>());
            if (!oldOccupied.contains(oldRoom)) {
                JOptionPane.showMessageDialog(CardManagementGui.this, "Room not assigned!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String newFloorKey = "floor_" + floorSelector.getSelectedItem().toString();
            String newRoom = roomSelector.getSelectedItem() != null ? (String) roomSelector.getSelectedItem() : null;
            if (newRoom == null) {
                JOptionPane.showMessageDialog(CardManagementGui.this, "No available new room on the selected floor.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Set<String> newOccupied = occupiedRooms.getOrDefault(newFloorKey, new HashSet<>());
            if (newOccupied.contains(newRoom)) {
                JOptionPane.showMessageDialog(CardManagementGui.this, "The selected new room is already occupied!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // ดำเนินการย้ายห้อง: ลบสถานะจากห้องเดิมและเพิ่มสถานะในห้องใหม่
            oldOccupied.remove(oldRoom);
            RoomLogger.logRoomEvent(oldFloorKey, oldRoom, userName, "Moved Out");
            newOccupied.add(newRoom);
            RoomLogger.logRoomEvent(newFloorKey, newRoom, userName, "Moved In");
            occupiedRooms.put(oldFloorKey, oldOccupied);
            occupiedRooms.put(newFloorKey, newOccupied);
            refreshRooms(oldFloorKey);
            refreshRooms(newFloorKey);
            JOptionPane.showMessageDialog(CardManagementGui.this, "Room moved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }

        // ยกเลิกการจองห้อง - เฉพาะ Admin เท่านั้น (userName ต้องเป็น "Admin")
        public void cancelRoom() {
            String userName = userNameField.getText().trim();
            if (!userName.equals("Admin")) {
                JOptionPane.showMessageDialog(CardManagementGui.this,
                        "Only Admin can perform room cancellation.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String enteredCode = new String(authCodeField.getPassword());
            if (!enteredCode.equals(AUTH_CODE)) {
                JOptionPane.showMessageDialog(CardManagementGui.this, "Invalid Auth Code!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String currentFloorInput = JOptionPane.showInputDialog(CardManagementGui.this, "Enter current floor number:");
            String currentRoomInput = JOptionPane.showInputDialog(CardManagementGui.this, "Enter current room number:");
            if (currentFloorInput == null || currentRoomInput == null ||
                currentFloorInput.trim().isEmpty() || currentRoomInput.trim().isEmpty()) {
                JOptionPane.showMessageDialog(CardManagementGui.this, "Current floor and room are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String floorKey = "floor_" + currentFloorInput.trim();
            String room = currentRoomInput.trim();
            Set<String> occupied = occupiedRooms.getOrDefault(floorKey, new HashSet<>());
            if (!occupied.contains(room)) {
                JOptionPane.showMessageDialog(CardManagementGui.this, "Room not assigned!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            occupied.remove(room);
            RoomLogger.logRoomEvent(floorKey, room, userName, "Cancelled");
            refreshRooms(floorKey);
            JOptionPane.showMessageDialog(CardManagementGui.this, "Room cancelled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }

        // แสดงหน้าต่างตรวจสอบการเข้าถึง (Access Check)
        public void showAccessCheckWindow() {
            JDialog accessDialog = new JDialog(CardManagementGui.this, "Access Verification", true);
            accessDialog.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            JTextField userField = new JTextField(15);
            JComboBox<String> floorBox = new JComboBox<>(new String[]{"1", "2", "3", "4", "5", "6", "7", "8"});
            JButton verifyButton = new JButton("Verify Access");

            gbc.gridx = 0;
            gbc.gridy = 0;
            accessDialog.add(new JLabel("User Name:"), gbc);
            gbc.gridx = 1;
            accessDialog.add(userField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            accessDialog.add(new JLabel("Floor:"), gbc);
            gbc.gridx = 1;
            accessDialog.add(floorBox, gbc);
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            accessDialog.add(verifyButton, gbc);

            verifyButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String user = userField.getText().trim();
                    String floor = "floor_" + floorBox.getSelectedItem().toString();
                    boolean accessGranted = AccessControlSystem.getInstance().verifyAccess(user, floor);
                    String message = accessGranted ? "Access granted for " + user : "Access denied for " + user;
                    JOptionPane.showMessageDialog(accessDialog, message, "Access Verification", JOptionPane.INFORMATION_MESSAGE);
                }
            });

            accessDialog.pack();
            accessDialog.setLocationRelativeTo(CardManagementGui.this);
            accessDialog.setVisible(true);
        }
    }

    // คลาสสำหรับวาด Background Image
    private static class BackgroundPanel extends JPanel {
        private final Image backgroundImage;

        public BackgroundPanel(String fileName) {
            backgroundImage = new ImageIcon(fileName).getImage();
            setLayout(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CardManagementGui().setVisible(true));
    }
}
