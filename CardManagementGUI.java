// CardManagementGui.java
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;

public class CardManagementGui extends JFrame {
    // UI Components
    private final JComboBox<String> floorSelector;
    private final JComboBox<String> roomSelector;
    private final JTextField userNameField;
    private final JPasswordField authCodeField;
    private final JButton addRoomButton;
    private final JButton moveRoomButton;
    private final JButton cancelRoomButton;
    
    // Map สำหรับเก็บข้อมูลห้องที่ถูกจองแล้ว
    private final Map<String, Set<String>> occupiedRooms;
    private static final String AUTH_CODE = "admin123";
    
    public CardManagementGui() {
        setTitle("Card Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        
        occupiedRooms = new HashMap<>();
        loadOccupiedRooms();
        
        // สร้าง UI Components
        userNameField = new JTextField(15);
        authCodeField = new JPasswordField(15);
        addRoomButton = new JButton("Add Room");
        moveRoomButton = new JButton("Move Room");
        cancelRoomButton = new JButton("Cancel Room");
        
        String[] floors = {"1", "2", "3", "4", "5", "6", "7", "8"};
        floorSelector = new JComboBox<>(floors);
        roomSelector = new JComboBox<>();
        
        // สร้าง BackgroundPanel สำหรับวาด Background Image
        BackgroundPanel backgroundPanel = new BackgroundPanel("background.jpg");
        backgroundPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // วาง Layout ลงใน backgroundPanel
        gbc.gridx = 0; gbc.gridy = 0;
        backgroundPanel.add(new JLabel("Select Floor:"), gbc);
        gbc.gridx = 1;
        backgroundPanel.add(floorSelector, gbc);
        gbc.gridx = 2;
        backgroundPanel.add(new JLabel("Select Room:"), gbc);
        gbc.gridx = 3;
        backgroundPanel.add(roomSelector, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 4;
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
        
        add(backgroundPanel);
        
        // Event Listeners
        floorSelector.addActionListener(e -> updateAvailableRooms());
        addRoomButton.addActionListener(e -> addRoom());
        moveRoomButton.addActionListener(e -> moveRoom());
        cancelRoomButton.addActionListener(e -> cancelRoom());
        
        updateAvailableRooms();
    }
    
    // เมธอดโหลดข้อมูลห้องที่จองแล้ว
    private void loadOccupiedRooms() {
        for (int i = 1; i <= 8; i++) {
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
    
    // เมธอดอัปเดตรายการห้องที่ว่าง
    private void updateAvailableRooms() {
        roomSelector.removeAllItems();
        String selectedFloor = "floor_" + floorSelector.getSelectedItem().toString();
        Set<String> occupied = occupiedRooms.getOrDefault(selectedFloor, new HashSet<>());
        for (int i = 1; i <= 20; i++) {
            String roomNumber = String.valueOf(i);
            if (!occupied.contains(roomNumber)) {
                roomSelector.addItem(roomNumber);
            }
        }
    }
    
    // เมธอดสำหรับจองห้องใหม่
    private void addRoom() {
        if (roomSelector.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "No available rooms on this floor.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String roomNumber = roomSelector.getSelectedItem().toString();
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
        
        RoomLogger.logRoomEvent(selectedFloor, roomNumber, userName, "Added");
        occupiedRooms.computeIfAbsent(selectedFloor, k -> new HashSet<>()).add(roomNumber);
        updateAvailableRooms();
        JOptionPane.showMessageDialog(this, "Room added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // เมธอดสำหรับย้ายห้อง
    private void moveRoom() {
        String enteredCode = new String(authCodeField.getPassword());
        if (!enteredCode.equals("admin123")) {
            JOptionPane.showMessageDialog(this, "Invalid Auth Code!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String currentFloorInput = JOptionPane.showInputDialog(this, "Enter your CURRENT floor number:");
        String currentRoomInput = JOptionPane.showInputDialog(this, "Enter your CURRENT room number:");
        if (currentFloorInput == null || currentRoomInput == null ||
            currentFloorInput.trim().isEmpty() || currentRoomInput.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Current floor and room are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String oldFloorKey = "floor_" + currentFloorInput.trim();
        String oldRoom = currentRoomInput.trim();
        Set<String> oldOccupied = occupiedRooms.getOrDefault(oldFloorKey, new HashSet<>());
        if (!oldOccupied.contains(oldRoom)) {
            JOptionPane.showMessageDialog(this, "You are not assigned to that room!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String newFloorKey = "floor_" + floorSelector.getSelectedItem().toString();
        String newRoom = roomSelector.getSelectedItem() != null ? roomSelector.getSelectedItem().toString() : null;
        if (newRoom == null) {
            JOptionPane.showMessageDialog(this, "No available new room on the selected floor.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Set<String> newOccupied = occupiedRooms.getOrDefault(newFloorKey, new HashSet<>());
        if (newOccupied.contains(newRoom)) {
            JOptionPane.showMessageDialog(this, "The selected new room is already occupied!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String userName = userNameField.getText().trim();
        if (userName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your user name", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        oldOccupied.remove(oldRoom);
        RoomLogger.logRoomEvent(oldFloorKey, oldRoom, userName, "Moved Out");
        newOccupied.add(newRoom);
        RoomLogger.logRoomEvent(newFloorKey, newRoom, userName, "Moved In");
        occupiedRooms.put(oldFloorKey, oldOccupied);
        occupiedRooms.put(newFloorKey, newOccupied);
        JOptionPane.showMessageDialog(this, "Room moved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        updateAvailableRooms();
    }
    
    // เมธอดสำหรับยกเลิกการจองห้อง
    private void cancelRoom() {
        String enteredCode = new String(authCodeField.getPassword());
        if (!enteredCode.equals("admin123")) {
            JOptionPane.showMessageDialog(this, "This line only use for admin only", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String currentFloorInput = JOptionPane.showInputDialog(this, "Enter your CURRENT floor number:");
        String currentRoomInput = JOptionPane.showInputDialog(this, "Enter your CURRENT room number:");
        if (currentFloorInput == null || currentRoomInput == null ||
            currentFloorInput.trim().isEmpty() || currentRoomInput.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Current floor and room are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String floorKey = "floor_" + currentFloorInput.trim();
        String room = currentRoomInput.trim();
        Set<String> occupied = occupiedRooms.getOrDefault(floorKey, new HashSet<>());
        if (!occupied.contains(room)) {
            JOptionPane.showMessageDialog(this, "You are not assigned to that room!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String userName = userNameField.getText().trim();
        if (userName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your user name", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        occupied.remove(room);
        RoomLogger.logRoomEvent(floorKey, room, userName, "Cancelled");
        JOptionPane.showMessageDialog(this, "Room cancelled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        updateAvailableRooms();
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
