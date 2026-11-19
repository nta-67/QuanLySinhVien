package qlsv;

import java.sql.Connection;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import qlsv.db.DBConnection;
import qlsv.ui.MainFrame;

public class App {

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // dùng giao diện mặc định
        }

        SwingUtilities.invokeLater(() -> {
            try (Connection c = DBConnection.getConnection()) {
                new MainFrame().setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Không kết nối được cơ sở dữ liệu QuanLySinhVien.\n\n"
                        + "1. Kiểm tra SQL Server đang chạy và đã chạy script database/QuanLySinhVien.sql.\n"
                        + "2. Kiểm tra tài khoản/mật khẩu trong file DBConnection.java.\n"
                        + "3. Bật TCP/IP (cổng 1433) trong SQL Server Configuration Manager.\n\n"
                        + "Chi tiết: " + e.getMessage(),
                        "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
