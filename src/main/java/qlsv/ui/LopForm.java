package qlsv.ui;

import java.awt.BorderLayout;
import java.sql.SQLException;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import qlsv.dao.LookupDAO;
import qlsv.dao.LopDAO;
import qlsv.model.Item;
import qlsv.model.Lop;

public class LopForm extends JFrame {

    private final JTextField txtMa = new JTextField();
    private final JTextField txtTen = new JTextField();
    private final JTextField txtKhoaHoc = new JTextField();
    private final JComboBox<Item> cboKhoa = new JComboBox<>();
    private final JTextField txtSearch = new JTextField(25);
    private final DefaultTableModel model = UI.tableModel("Mã lớp", "Tên lớp", "Khóa học", "Mã khoa");
    private final JTable table = UI.table(model);

    public LopForm() {
        super("Quản lý lớp");
        setLayout(new BorderLayout(8, 8));
        add(UI.searchBar(txtSearch, () -> load(txtSearch.getText()), this::hienTatCa), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        south.add(UI.fieldGrid("Mã lớp:", txtMa, "Tên lớp:", txtTen,
                "Khóa học (vd K15):", txtKhoaHoc, "Khoa:", cboKhoa), BorderLayout.CENTER);
        south.add(UI.buttonBar(UI.button("Thêm", this::them), UI.button("Sửa", this::sua),
                UI.button("Xóa", this::xoa), UI.button("Làm mới", this::lamMoi)), BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFromTable();
            }
        });
        UI.setup(this, 800, 520);
        try {
            UI.fillCombo(cboKhoa, LookupDAO.khoa());
        } catch (SQLException ex) {
            UI.error(this, ex);
        }
        load("");
    }

    private void load(String keyword) {
        try {
            model.setRowCount(0);
            for (Lop l : LopDAO.search(keyword)) {
                model.addRow(new Object[]{l.getMaLop(), l.getTenLop(), l.getKhoaHoc(), l.getMaKhoa()});
            }
        } catch (SQLException ex) {
            UI.error(this, ex);
        }
    }

    private void hienTatCa() {
        txtSearch.setText("");
        load("");
    }

    private void fillFromTable() {
        int r = table.getSelectedRow();
        if (r < 0) {
            return;
        }
        txtMa.setText(UI.cell(model, r, 0));
        txtTen.setText(UI.cell(model, r, 1));
        txtKhoaHoc.setText(UI.cell(model, r, 2));
        UI.select(cboKhoa, UI.cell(model, r, 3));
        txtMa.setEnabled(false);
    }

    private void lamMoi() {
        txtMa.setText("");
        txtTen.setText("");
        txtKhoaHoc.setText("");
        txtMa.setEnabled(true);
        table.clearSelection();
        hienTatCa();
    }

    private Lop read() {
        String ma = txtMa.getText().trim();
        String ten = txtTen.getText().trim();
        String khoa = UI.selected(cboKhoa);
        if (ma.isEmpty() || ten.isEmpty()) {
            UI.warn(this, "Vui lòng nhập Mã lớp và Tên lớp.");
            return null;
        }
        if (khoa == null) {
            UI.warn(this, "Chưa có khoa nào trong cơ sở dữ liệu. Hãy chạy script dữ liệu mẫu.");
            return null;
        }
        return new Lop(ma, ten, txtKhoaHoc.getText().trim(), khoa);
    }

    private void them() {
        if (!txtMa.isEnabled()) {
            UI.warn(this, "Đang chọn một dòng trong bảng. Bấm \"Làm mới\" trước khi thêm mới.");
            return;
        }
        Lop l = read();
        if (l == null) {
            return;
        }
        try {
            LopDAO.insert(l);
            UI.info(this, "Thêm lớp thành công.");
            lamMoi();
        } catch (SQLException ex) {
            UI.error(this, ex);
        }
    }

    private void sua() {
        if (table.getSelectedRow() < 0) {
            UI.warn(this, "Hãy chọn lớp cần sửa trong bảng.");
            return;
        }
        Lop l = read();
        if (l == null) {
            return;
        }
        try {
            LopDAO.update(l);
            UI.info(this, "Cập nhật lớp thành công.");
            lamMoi();
        } catch (SQLException ex) {
            UI.error(this, ex);
        }
    }

    private void xoa() {
        int r = table.getSelectedRow();
        if (r < 0) {
            UI.warn(this, "Hãy chọn lớp cần xóa trong bảng.");
            return;
        }
        String ma = UI.cell(model, r, 0);
        if (!UI.confirm(this, "Xóa lớp " + ma + "?")) {
            return;
        }
        try {
            LopDAO.delete(ma);
            UI.info(this, "Đã xóa lớp.");
            lamMoi();
        } catch (SQLException ex) {
            UI.error(this, ex);
        }
    }
}
