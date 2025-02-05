import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.*;

public class CardManagementGUI extends JFrame {
    private final ArrayList<AccessCard> cards = new ArrayList<>();
    private final HashSet<String> occupiedRooms = new HashSet<>();
    private final JTextField nameField;
    private final JComboBox<Integer> floorCombo, roomCombo;
    private final JButton addButton, moveButton;
    private final JList<String> cardList;
    private final  DefaultListModel<String> listModel;
    private final JTextField moveCodeField;

    public CardManagementGUI() {
        setTitle("Condo Access Card Management");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel สำหรับกรอกข้อมูล
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Floor:"));
        floorCombo = new JComboBox<>();
        for (int i = 1; i <= 10; i++) floorCombo.addItem(i);
        inputPanel.add(floorCombo);

        inputPanel.add(new JLabel("Room:"));
        roomCombo = new JComboBox<>();
        for (int i = 1; i <= 50; i++) roomCombo.addItem(i);
        inputPanel.add(roomCombo);

        add(inputPanel, BorderLayout.NORTH);

        // ปุ่มเพิ่มบัตร
        addButton = new JButton("Add Card");
        addButton.addActionListener(e -> addCard());
        add(addButton, BorderLayout.CENTER);

        // ปุ่มสำหรับย้ายห้อง
        moveButton = new JButton("Move Room");
        moveButton.addActionListener(e -> moveCard());
        add(moveButton, BorderLayout.SOUTH);

        // Panel สำหรับการย้ายห้อง
        JPanel movePanel = new JPanel(new FlowLayout());
        movePanel.add(new JLabel("Enter Move Code:"));
        moveCodeField = new JTextField(10);
        movePanel.add(moveCodeField);
        add(movePanel, BorderLayout.SOUTH);

        // List แสดงบัตรทั้งหมด
        listModel = new DefaultListModel<>();
        cardList = new JList<>(listModel);
        add(new JScrollPane(cardList), BorderLayout.SOUTH);

        setVisible(true);
    }

    private void addCard() {
        String name = nameField.getText();
        int floor = (int) floorCombo.getSelectedItem();
        int room = (int) roomCombo.getSelectedItem();
        String roomKey = floor + "-" + room;

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // เช็คว่าห้องนี้มีเจ้าของอยู่แล้วหรือไม่
        if (occupiedRooms.contains(roomKey)) {
            JOptionPane.showMessageDialog(this, "Room " + room + " on floor " + floor + " is already occupied!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // เพิ่มบัตรใหม่
        AccessCard card = new AccessCard(name, floor, room);
        cards.add(card);
        occupiedRooms.add(roomKey); // บันทึกว่าห้องนี้ถูกใช้แล้ว
        listModel.addElement(card.getDetails());

        nameField.setText("");
    }

    // ฟังก์ชันย้ายห้อง
    private void moveCard() {
        String moveCode = moveCodeField.getText();
        for (AccessCard card : cards) {
            if (card.getId().equals(moveCode)) {
                int newFloor = (int) floorCombo.getSelectedItem();
                int newRoom = (int) roomCombo.getSelectedItem();
                String newRoomKey = newFloor + "-" + newRoom;
                if (occupiedRooms.contains(newRoomKey)) {
                    JOptionPane.showMessageDialog(this, "Room " + newRoom + " on floor " + newFloor + " is already occupied!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    occupiedRooms.remove(card.getFloor() + "-" + card.getRoom()); // ลบห้องเก่า
                    card.setFloor(newFloor);
                    card.setRoom(newRoom);
                    occupiedRooms.add(newRoomKey); // เพิ่มห้องใหม่
                    listModel.clear();
                    for (AccessCard updatedCard : cards) {
                        listModel.addElement(updatedCard.getDetails());
                    }
                    JOptionPane.showMessageDialog(this, "Room moved successfully!");
                }
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Invalid Move Code!", "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CardManagementGUI());
    }
}

