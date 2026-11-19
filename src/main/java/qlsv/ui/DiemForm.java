package qlsv.ui;

import java.awt.BorderLayout;
import java.sql.SQLException;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import qlsv.dao.DiemDAO;
import qlsv.dao.LookupDAO;
import qlsv.model.Diem;
import qlsv.model.Item;

public class DiemForm extends JFrame {

    private final JComboBox<Item> cboSV = new JComboBox<>();
    private final JComboBox<Item> cboMH = new JComboBox<>();
    private final JComboBox<String> cboHocKy = new JComboBox<>(new String[]{"1", "2", "3"});
    private final JTextField txtNamHoc = new JTextField("2025-2026");
    private final JTextField txtQT = new JTextField();
    private final JTextField txtThi = new JTextField();
    private final JLabel lblTK = new JLabel("(tự tính: 30% quá trình + 70% thi)");
    private final JTextField txtSearch = new JTextField(25);
    private final DefaultTableModel model = UI.tableModel("Mã SV", "Họ tên", "Mã môn", "Tên môn",
            "Học kỳ", "Năm học", "Điểm QT", "Điểm thi", "Điểm TK");
    private final JTable table = UI.table(model);

    public DiemForm() {
        super("Quản lý điểm");
        setLayout(new BorderLayout(8, 8));
        add(UI.searchBar(txtSearch, () -> load(txtSearch.getText()), this::hienTatCa), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        south.add(UI.fieldGrid(
                "Sinh viên:", cboSV, "Môn học:", cboMH,
                "Học kỳ:", cboHocKy, "Năm học (vd 2025-2026):", txtNamHoc,
                "Điểm quá trình (0-10):", txtQT, "Điểm thi (0-10):", txtThi,
                "Điểm tổng kết:", lblTK), BorderLayout.CENTER);
        south.add(UI.buttonBar(UI.button("Thêm", this::them), UI.button("Sửa", this::sua),
                UI.button("Xóa", this::xoa), UI.button("Làm mới", this::lamMoi)), BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFromTable();
            }
        });
        UI.setup(this, 1100, 680);
        loadCombos();
        load("");
    }

    private void loadCombos() {
        try {
            UI.fillCombo(cboSV, LookupDAO.sinhVien());
            UI.fillCombo(cboMH, LookupDAO.monHoc());
        } catch (SQLException ex) {
            UI.error(this, ex);
        }
    }

    private void load(String keyword) {
        try {
            model.setRowCount(0);
            for (Diem d : DiemDAO.search(keyword)) {
                model.addRow(new Object[]{d.getMaSV(), d.getHoTen(), d.getMaMH(), d.getTenMH(),
                        d.getHocKy(), d.getNamHoc(), UI.fmt(d.getDiemQuaTrinh()),
                        UI.fmt(d.getDiemThi()), UI.fmt(d.getDiemTongKet())});
            }
        } catch (SQLException ex) {
            UI.error(this, ex);
        }
    }

    private void hienTatCa() {
        txtSearch.setText("");
        load("");
    }

    private void setKeyEnabled(boolean enabled) {
        cboSV.setEnabled(enabled);
        cboMH.setEnabled(enabled);
        cboHocKy.setEnabled(enabled);
        txtNamHoc.setEnabled(enabled);
    }

    private void fillFromTable() {
        int r = table.getSelectedRow();
        if (r < 0) {
            return;
        }
        UI.select(cboSV, UI.cell(model, r, 0));
        UI.select(cboMH, UI.cell(model, r, 2));
        cboHocKy.setSelectedItem(UI.cell(model, r, 4));
        txtNamHoc.setText(UI.cell(model, r, 5));
        txtQT.setText(UI.cell(model, r, 6));
        txtThi.setText(UI.cell(model, r, 7));
        lblTK.setText(UI.cell(model, r, 8));
        setKeyEnabled(false);
    }

    private void lamMoi() {
        loadCombos();
        txtNamHoc.setText("2025-2026");
        txtQT.setText("");
        txtThi.setText("");
        lblTK.setText("(tự tính: 30% quá trình + 70% thi)");
        cboHocKy.setSelectedIndex(0);
        setKeyEnabled(true);
        table.clearSelection();
        hienTatCa();
    }

    /** Điểm rỗng -> null; ngoài khoảng 0-10 hoặc sai định dạng -> ném IllegalArgumentException. */
    private Double parseDiem(String text, String tenDiem) {
        String t = text.trim().replace(',', '.');
        if (t.isEmpty()) {
            return null;
        }
        double v;
        try {
            v = Double.parseDouble(t);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(tenDiem + " phải là số.");
        }
        if (v < 0 || v > 10) {
            throw new IllegalArgumentException(tenDiem + " phải nằm trong khoảng 0 đến 10.");
        }
        return v;
    }

    private Diem read() {
        String sv = UI.selected(cboSV);
        String mh = UI.selected(cboMH);
        if (sv == null || mh == null) {
            UI.warn(this, "Cần có sinh viên và môn học trước khi nhập điểm.");
            return null;
        }
        String nam = txtNamHoc.getText().trim();
        if (!nam.matches("\\d{4}-\\d{4}")) {
            UI.warn(this, "Năm học phải có dạng 2025-2026.");
            return null;
        }
        Double qt;
        Double thi;
        try {
            qt = parseDiem(txtQT.getText(), "Điểm quá trình");
            thi = parseDiem(txtThi.getText(), "Điểm thi");
        } catch (IllegalArgumentException ex) {
            UI.warn(this, ex.getMessage());
            return null;
        }
        return new Diem(sv, null, mh, null, Integer.parseInt((String) cboHocKy.getSelectedItem()),
                nam, qt, thi, null);
    }

    private void them() {
        if (!cboSV.isEnabled()) {
            UI.warn(this, "Đang chọn một dòng trong bảng. Bấm \"Làm mới\" trước khi thêm mới.");
            return;
        }
        Diem d = read();
        if (d == null) {
            return;
        }
        try {
            DiemDAO.insert(d);
            UI.info(this, "Thêm điểm thành công.");
            lamMoi();
        } catch (SQLException ex) {
            UI.error(this, ex);
        }
    }

    private void sua() {
        if (table.getSelectedRow() < 0) {
            UI.warn(this, "Hãy chọn dòng điểm cần sửa trong bảng.");
            return;
        }
        Diem d = read();
        if (d == null) {
            return;
        }
        try {
            DiemDAO.update(d);
            UI.info(this, "Cập nhật điểm thành công.");
            lamMoi();
        } catch (SQLException ex) {
            UI.error(this, ex);
        }
    }

    private void xoa() {
        int r = table.getSelectedRow();
        if (r < 0) {
            UI.warn(this, "Hãy chọn dòng điểm cần xóa trong bảng.");
            return;
        }
        String sv = UI.cell(model, r, 0);
        String mh = UI.cell(model, r, 2);
        int hk = Integer.parseInt(UI.cell(model, r, 4));
        String nam = UI.cell(model, r, 5);
        if (!UI.confirm(this, "Xóa điểm môn " + mh + " của sinh viên " + sv + "?")) {
            return;
        }
        try {
            DiemDAO.delete(sv, mh, hk, nam);
            UI.info(this, "Đã xóa điểm.");
            lamMoi();
        } catch (SQLException ex) {
            UI.error(this, ex);
        }
    }
}
