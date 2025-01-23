import javax.swing.*; // นำเข้า Swing
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

    public class Main {
        public static void main(String[] args) {
            // สร้าง frame หรือหน้าต่าง
            JFrame frame = new JFrame("Simple GUI Example");

            // ขยายเต็มหน้าจอ
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

            // กำหนดการทำงานเมื่อปิดหน้าต่าง
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // ตั้งค่า Layout ของ frame ให้เป็น FlowLayout และจัดให้อยู่ตรงกลาง พร้อมขยับลงมาจากด้านบน
            frame.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 50)); // 50 คือระยะห่างในแนวตั้ง

            // สร้าง JPanel สำหรับครอบปุ่ม
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 50));  // ตั้ง layout ของ panel

            // สร้างปุ่ม
            JButton button = new JButton("Click Me!");
            button.setPreferredSize(new Dimension(200, 100)); // กำหนดขนาดปุ่ม

            // เพิ่มปุ่มเข้าไปใน panel
            panel.add(button);

            // เพิ่ม JScrollPane ครอบ JPanel
            JScrollPane scrollPane = new JScrollPane(panel);

            // เพิ่ม JScrollPane ลงใน frame
            frame.add(scrollPane);

            // ทำให้หน้าต่างแสดงขึ้นมา
            frame.setSize(800, 600); // ตั้งขนาดหน้าต่าง
            frame.setVisible(true);
        }
    }

