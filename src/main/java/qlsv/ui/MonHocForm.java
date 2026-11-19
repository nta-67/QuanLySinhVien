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
import qlsv.dao.MonHocDAO;
import qlsv.model.Item;
import qlsv.model.MonHoc;

public class MonHocForm extends JFrame {

    private final JTextField txtMa = new JTextField();
    private final JTextField txtTen = new JTextField();
    private final JTextField txtTinChi = new JTextField();
    private final JComboBox<Item> cboKhoa = new JComboBox<>();
    private final JTextField txtSearch = new JTextField(25);
    private final DefaultTableModel model = UI.tableModel("Mã môn", "Tên môn học", "Số tín chỉ", "Mã khoa");
    private final JTable table = UI.table(model);

    public MonHocForm() {
        super("Quản lý môn học");
        setLayout(new BorderLayout(8, 8));
        add(UI.searchBar(txtSearch, () -> load(txtSearch.getText()), this::hienTatCa), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        south.add(UI.fieldGrid("Mã môn:", txtMa, "Tên môn học:", txtTen,
                "Số tín chỉ (1-10):", txtTinChi, "Khoa:", cboKhoa), BorderLayout.CENTER);
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
            for (MonHoc m : MonHocDAO.search(keyword)) {
                model.addRow(new Object[]{m.getMaMH(), m.getTenMH(), m.getSoTinChi(), m.getMaKhoa()});
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
        txtTinChi.setText(UI.cell(model, r, 2));
        UI.select(cboKhoa, UI.cell(model, r, 3));
        txtMa.setEnabled(false);
    }

    private void lamMoi() {
        txtMa.setText("");
        txtTen.setText("");
        txtTinChi.setText("");
        txtMa.setEnabled(true);
        table.clearSelection();
        hienTatCa();
    }

    private MonHoc read() {
        String ma = txtMa.getText().trim();
        String ten = txtTen.getText().trim();
        String khoa = UI.selected(cboKhoa);
        if (ma.isEmpty() || ten.isEmpty() || txtTinChi.getText().trim().isEmpty()) {
            UI.warn(this, "Vui lòng nhập Mã môn, Tên môn học và Số tín chỉ.");
            return null;
        }
        int tc;
        try {
            tc = Integer.parseInt(txtTinChi.getText().trim());
        } catch (NumberFormatException ex) {
            UI.warn(this, "Số tín chỉ phải là số nguyên.");
            return null;
        }
        if (tc < 1 || tc > 10) {
            UI.warn(this, "Số tín chỉ phải từ 1 đến 10.");
            return null;
        }
        if (khoa == null) {
            UI.warn(this, "Chưa có khoa nào trong cơ sở dữ liệu. Hãy chạy script dữ liệu mẫu.");
            return null;
        }
        return new MonHoc(ma, ten, tc, khoa);
    }

    private void them() {
        if (!txtMa.isEnabled()) {
            UI.warn(this, "Đang chọn một dòng trong bảng. Bấm \"Làm mới\" trước khi thêm mới.");
            return;
        }
        MonHoc m = read();
        if (m == null) {
            return;
        }
        try {
            MonHocDAO.insert(m);
            UI.info(this, "Thêm môn học thành công.");
            lamMoi();
        } catch (SQLException ex) {
            UI.error(this, ex);
        }
    }

    private void sua() {
        if (table.getSelectedRow() < 0) {
            UI.warn(this, "Hãy chọn môn học cần sửa trong bảng.");
            return;
        }
        MonHoc m = read();
        if (m == null) {
            return;
        }
        try {
            MonHocDAO.update(m);
            UI.info(this, "Cập nhật môn học thành công.");
            lamMoi();
        } catch (SQLException ex) {
            UI.error(this, ex);
        }
    }

    private void xoa() {
        int r = table.getSelectedRow();
        if (r < 0) {
            UI.warn(this, "Hãy chọn môn học cần xóa trong bảng.");
            return;
        }
        String ma = UI.cell(model, r, 0);
        if (!UI.confirm(this, "Xóa môn học " + ma + "?")) {
            return;
        }
        try {
            MonHocDAO.delete(ma);
            UI.info(this, "Đã xóa môn học.");
            lamMoi();
        } catch (SQLException ex) {
            UI.error(this, ex);
        }
    }
}
