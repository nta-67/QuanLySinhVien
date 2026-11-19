package qlsv.ui;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import qlsv.dao.LookupDAO;
import qlsv.dao.SinhVienDAO;
import qlsv.model.Item;
import qlsv.model.SinhVien;

public class SinhVienForm extends JFrame {

    private final JTextField txtMa = new JTextField();
    private final JTextField txtTen = new JTextField();
    private final JComboBox<String> cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
    private final JTextField txtNgaySinh = new JTextField();
    private final JTextField txtDiaChi = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JTextField txtSdt = new JTextField();
    private final JComboBox<Item> cboLop = new JComboBox<>();
    private final JTextField txtSearch = new JTextField(25);
    private final DefaultTableModel model = UI.tableModel(
            "Mã SV", "Họ tên", "Giới tính", "Ngày sinh", "Địa chỉ", "Email", "SĐT", "Mã lớp");
    private final JTable table = UI.table(model);

    public SinhVienForm() {
        super("Quản lý sinh viên");
        setLayout(new BorderLayout(8, 8));
        add(UI.searchBar(txtSearch, () -> load(txtSearch.getText()), this::hienTatCa), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        south.add(UI.fieldGrid(
                "Mã SV:", txtMa, "Họ tên:", txtTen,
                "Giới tính:", cboGioiTinh, "Ngày sinh (dd/MM/yyyy):", txtNgaySinh,
                "Địa chỉ:", txtDiaChi, "Email:", txtEmail,
                "Số điện thoại:", txtSdt, "Lớp:", cboLop), BorderLayout.CENTER);
        south.add(UI.buttonBar(UI.button("Thêm", this::them), UI.button("Sửa", this::sua),
                UI.button("Xóa", this::xoa), UI.button("Làm mới", this::lamMoi)), BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFromTable();
            }
        });
        UI.setup(this, 1050, 680);
        try {
            UI.fillCombo(cboLop, LookupDAO.lop());
        } catch (SQLException ex) {
            UI.error(this, ex);
        }
        load("");
    }

    private void load(String keyword) {
        try {
            model.setRowCount(0);
            for (SinhVien s : SinhVienDAO.search(keyword)) {
                model.addRow(new Object[]{s.getMaSV(), s.getHoTen(), s.getGioiTinh(),
                        s.getNgaySinh().format(UI.DATE), s.getDiaChi(), s.getEmail(),
                        s.getSoDienThoai(), s.getMaLop()});
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
        cboGioiTinh.setSelectedItem(UI.cell(model, r, 2));
        txtNgaySinh.setText(UI.cell(model, r, 3));
        txtDiaChi.setText(UI.cell(model, r, 4));
        txtEmail.setText(UI.cell(model, r, 5));
        txtSdt.setText(UI.cell(model, r, 6));
        UI.select(cboLop, UI.cell(model, r, 7));
        txtMa.setEnabled(false);
    }

    private void lamMoi() {
        txtMa.setText("");
        txtTen.setText("");
        txtNgaySinh.setText("");
        txtDiaChi.setText("");
        txtEmail.setText("");
        txtSdt.setText("");
        cboGioiTinh.setSelectedIndex(0);
        txtMa.setEnabled(true);
        table.clearSelection();
        hienTatCa();
    }

    /** Đọc và kiểm tra dữ liệu nhập; trả về null nếu có lỗi (đã báo cho người dùng). */
    private SinhVien read() {
        String ma = txtMa.getText().trim();
        String ten = txtTen.getText().trim();
        String ngayStr = txtNgaySinh.getText().trim();
        if (ma.isEmpty() || ten.isEmpty() || ngayStr.isEmpty()) {
            UI.warn(this, "Vui lòng nhập Mã SV, Họ tên và Ngày sinh.");
            return null;
        }
        LocalDate ngay;
        try {
            ngay = LocalDate.parse(ngayStr, UI.DATE);
        } catch (DateTimeParseException ex) {
            UI.warn(this, "Ngày sinh không hợp lệ. Nhập theo dạng dd/MM/yyyy, ví dụ 12/03/2005.");
            return null;
        }
        if (ngay.isAfter(LocalDate.now())) {
            UI.warn(this, "Ngày sinh không được lớn hơn ngày hiện tại.");
            return null;
        }
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            UI.warn(this, "Email không đúng định dạng.");
            return null;
        }
        String sdt = txtSdt.getText().trim();
        if (!sdt.isEmpty() && !sdt.matches("\\d{9,11}")) {
            UI.warn(this, "Số điện thoại phải gồm 9 đến 11 chữ số.");
            return null;
        }
        String lop = UI.selected(cboLop);
        if (lop == null) {
            UI.warn(this, "Chưa có lớp nào. Hãy thêm lớp ở form Quản lý lớp trước.");
            return null;
        }
        return new SinhVien(ma, ten, (String) cboGioiTinh.getSelectedItem(), ngay,
                txtDiaChi.getText().trim(), email, sdt, lop);
    }

    private void them() {
        if (!txtMa.isEnabled()) {
            UI.warn(this, "Đang chọn một dòng trong bảng. Bấm \"Làm mới\" trước khi thêm mới.");
            return;
        }
        SinhVien s = read();
        if (s == null) {
            return;
        }
        try {
            SinhVienDAO.insert(s);
            UI.info(this, "Thêm sinh viên thành công.");
            lamMoi();
        } catch (SQLException ex) {
            UI.error(this, ex);
        }
    }

    private void sua() {
        if (table.getSelectedRow() < 0) {
            UI.warn(this, "Hãy chọn sinh viên cần sửa trong bảng.");
            return;
        }
        SinhVien s = read();
        if (s == null) {
            return;
        }
        try {
            SinhVienDAO.update(s);
            UI.info(this, "Cập nhật sinh viên thành công.");
            lamMoi();
        } catch (SQLException ex) {
            UI.error(this, ex);
        }
    }

    private void xoa() {
        int r = table.getSelectedRow();
        if (r < 0) {
            UI.warn(this, "Hãy chọn sinh viên cần xóa trong bảng.");
            return;
        }
        String ma = UI.cell(model, r, 0);
        if (!UI.confirm(this, "Xóa sinh viên " + ma + "? Điểm của sinh viên này cũng sẽ bị xóa.")) {
            return;
        }
        try {
            SinhVienDAO.delete(ma);
            UI.info(this, "Đã xóa sinh viên.");
            lamMoi();
        } catch (SQLException ex) {
            UI.error(this, ex);
        }
    }
}
