package qlsv.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import qlsv.db.DBConnection;
import qlsv.model.SinhVien;

public class SinhVienDAO {

    public static List<SinhVien> search(String keyword) throws SQLException {
        String sql = "SELECT MaSV, HoTen, GioiTinh, NgaySinh, DiaChi, Email, SoDienThoai, MaLop FROM SINHVIEN "
                + "WHERE MaSV LIKE ? OR HoTen LIKE ? ORDER BY MaSV";
        String k = "%" + keyword.trim() + "%";
        List<SinhVien> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, k);
            ps.setString(2, k);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new SinhVien(rs.getString(1), rs.getString(2), rs.getString(3),
                            rs.getDate(4).toLocalDate(), rs.getString(5), rs.getString(6),
                            rs.getString(7), rs.getString(8)));
                }
            }
        }
        return list;
    }

    public static void insert(SinhVien s) throws SQLException {
        DBConnection.execute("INSERT INTO SINHVIEN(MaSV, HoTen, GioiTinh, NgaySinh, DiaChi, Email, SoDienThoai, MaLop) "
                        + "VALUES(?,?,?,?,?,?,?,?)",
                s.getMaSV(), s.getHoTen(), s.getGioiTinh(), Date.valueOf(s.getNgaySinh()),
                DBConnection.emptyToNull(s.getDiaChi()), DBConnection.emptyToNull(s.getEmail()),
                DBConnection.emptyToNull(s.getSoDienThoai()), s.getMaLop());
    }

    public static void update(SinhVien s) throws SQLException {
        DBConnection.execute("UPDATE SINHVIEN SET HoTen=?, GioiTinh=?, NgaySinh=?, DiaChi=?, Email=?, SoDienThoai=?, MaLop=? "
                        + "WHERE MaSV=?",
                s.getHoTen(), s.getGioiTinh(), Date.valueOf(s.getNgaySinh()),
                DBConnection.emptyToNull(s.getDiaChi()), DBConnection.emptyToNull(s.getEmail()),
                DBConnection.emptyToNull(s.getSoDienThoai()), s.getMaLop(), s.getMaSV());
    }

    public static void delete(String maSV) throws SQLException {
        DBConnection.execute("DELETE FROM SINHVIEN WHERE MaSV=?", maSV);
    }
}
