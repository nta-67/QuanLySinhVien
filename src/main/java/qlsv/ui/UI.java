package qlsv.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import qlsv.model.Item;

/** Các hàm dùng chung cho các form. */
public final class UI {

    public static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private UI() {
    }

    // ---------- Thông báo ----------
    public static void info(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void warn(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Chú ý", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean confirm(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Xác nhận",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /** Hiển thị lỗi SQL bằng tiếng Việt dễ hiểu. */
    public static void error(Component parent, Exception e) {
        String m = e.getMessage() == null ? e.toString() : e.getMessage();
        String low = m.toLowerCase();
        String text;
        if (low.contains("login failed") || low.contains("tcp/ip") || low.contains("cannot open database")
                || low.contains("connection")) {
            text = "Không kết nối được cơ sở dữ liệu.\n"
                    + "Hãy kiểm tra SQL Server đã chạy chưa và tài khoản/mật khẩu trong file DBConnection.java.";
        } else if (low.contains("unique key")) {
            text = "Giá trị bị trùng với bản ghi khác (ví dụ email đã tồn tại).";
        } else if (low.contains("primary key")) {
            text = "Mã này đã tồn tại, vui lòng nhập mã khác.";
        } else if (low.contains("reference constraint")) {
            text = "Không thể xóa vì dữ liệu đang được sử dụng ở bảng khác.";
        } else if (low.contains("foreign key")) {
            text = "Dữ liệu tham chiếu không hợp lệ (lớp, khoa, môn học hoặc sinh viên không tồn tại).";
        } else if (low.contains("check constraint")) {
            text = "Dữ liệu không hợp lệ (vi phạm ràng buộc của cơ sở dữ liệu).";
        } else {
            text = m;
        }
        JOptionPane.showMessageDialog(parent, text, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    // ---------- Thành phần giao diện ----------
    public static void setup(JFrame f, int w, int h) {
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setSize(w, h);
        f.setLocationRelativeTo(null);
    }

    public static JButton button(String text, Runnable action) {
        JButton b = new JButton(text);
        b.addActionListener(e -> action.run());
        return b;
    }

    public static DefaultTableModel tableModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static JTable table(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setRowHeight(24);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setFillsViewportHeight(true);
        t.getTableHeader().setReorderingAllowed(false);
        return t;
    }

    /** Lưới nhập liệu 2 cặp (nhãn, ô nhập) trên mỗi dòng. Truyền lần lượt: "Nhãn:", component, ... */
    public static JPanel fieldGrid(Object... labelAndField) {
        JPanel p = new JPanel(new GridLayout(0, 4, 10, 8));
        p.setBorder(BorderFactory.createEmptyBorder(8, 10, 4, 10));
        for (int i = 0; i + 1 < labelAndField.length; i += 2) {
            p.add(new JLabel((String) labelAndField[i]));
            p.add((JComponent) labelAndField[i + 1]);
        }
        return p;
    }

    public static JPanel buttonBar(JButton... buttons) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        for (JButton b : buttons) {
            p.add(b);
        }
        return p;
    }

    public static JPanel searchBar(JTextField txt, Runnable search, Runnable showAll) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        p.add(new JLabel("Từ khóa tìm kiếm:"));
        p.add(txt);
        p.add(button("Tìm kiếm", search));
        p.add(button("Hiện tất cả", showAll));
        txt.addActionListener(e -> search.run());
        return p;
    }

    // ---------- ComboBox ----------
    public static void fillCombo(JComboBox<Item> cbo, List<Item> items) {
        cbo.removeAllItems();
        for (Item it : items) {
            cbo.addItem(it);
        }
    }

    public static void select(JComboBox<Item> cbo, String ma) {
        for (int i = 0; i < cbo.getItemCount(); i++) {
            if (cbo.getItemAt(i).getMa().equals(ma)) {
                cbo.setSelectedIndex(i);
                return;
            }
        }
    }

    /** Mã của mục đang chọn, null nếu chưa có mục nào. */
    public static String selected(JComboBox<Item> cbo) {
        Item it = (Item) cbo.getSelectedItem();
        return it == null ? null : it.getMa();
    }

    // ---------- Khác ----------
    public static String fmt(Double d) {
        return d == null ? "" : String.format("%.2f", d);
    }

    /** Giá trị ô trong bảng dưới dạng chuỗi, null -> "". */
    public static String cell(DefaultTableModel m, int row, int col) {
        Object v = m.getValueAt(row, col);
        return v == null ? "" : v.toString();
    }

    public static BorderLayout border() {
        return new BorderLayout(8, 8);
    }
}
