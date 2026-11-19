package qlsv.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MainFrame extends JFrame {

    public MainFrame() {
        super("Phần mềm quản lý sinh viên");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel title = new JLabel("QUẢN LÝ SINH VIÊN", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
        title.setBorder(BorderFactory.createEmptyBorder(24, 10, 10, 10));

        JPanel center = new JPanel(new GridLayout(2, 2, 16, 16));
        center.setBorder(BorderFactory.createEmptyBorder(16, 40, 40, 40));
        center.add(big("Quản lý lớp", () -> new LopForm().setVisible(true)));
        center.add(big("Quản lý sinh viên", () -> new SinhVienForm().setVisible(true)));
        center.add(big("Quản lý môn học", () -> new MonHocForm().setVisible(true)));
        center.add(big("Quản lý điểm", () -> new DiemForm().setVisible(true)));

        setLayout(new BorderLayout());
        add(title, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("Hệ thống");
        JMenuItem exit = new JMenuItem("Thoát");
        exit.addActionListener(e -> System.exit(0));
        menu.add(exit);
        bar.add(menu);
        setJMenuBar(bar);

        setSize(640, 420);
        setLocationRelativeTo(null);
    }

    private JButton big(String text, Runnable action) {
        JButton b = UI.button(text, action);
        b.setFont(b.getFont().deriveFont(Font.BOLD, 16f));
        return b;
    }
}
