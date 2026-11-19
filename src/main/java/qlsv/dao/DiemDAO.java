package qlsv.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import qlsv.db.DBConnection;
import qlsv.model.Diem;

public class DiemDAO {

    public static List<Diem> search(String keyword) throws SQLException {
        String sql = "SELECT d.MaSV, sv.HoTen, d.MaMH, mh.TenMH, d.HocKy, d.NamHoc, "
                + "d.DiemQuaTrinh, d.DiemThi, d.DiemTongKet "
                + "FROM DIEM d JOIN SINHVIEN sv ON d.MaSV = sv.MaSV JOIN MONHOC mh ON d.MaMH = mh.MaMH "
                + "WHERE d.MaSV LIKE ? OR sv.HoTen LIKE ? OR mh.TenMH LIKE ? ORDER BY d.MaSV, d.MaMH";
        String k = "%" + keyword.trim() + "%";
        List<Diem> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, k);
            ps.setString(2, k);
            ps.setString(3, k);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Diem(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                            rs.getInt(5), rs.getString(6),
                            DBConnection.getDouble(rs, 7), DBConnection.getDouble(rs, 8),
                            DBConnection.getDouble(rs, 9)));
                }
            }
        }
        return list;
    }

    public static void insert(Diem d) throws SQLException {
        DBConnection.execute("INSERT INTO DIEM(MaSV, MaMH, HocKy, NamHoc, DiemQuaTrinh, DiemThi) VALUES(?,?,?,?,?,?)",
                d.getMaSV(), d.getMaMH(), d.getHocKy(), d.getNamHoc(), d.getDiemQuaTrinh(), d.getDiemThi());
    }

    public static void update(Diem d) throws SQLException {
        DBConnection.execute("UPDATE DIEM SET DiemQuaTrinh=?, DiemThi=? WHERE MaSV=? AND MaMH=? AND HocKy=? AND NamHoc=?",
                d.getDiemQuaTrinh(), d.getDiemThi(), d.getMaSV(), d.getMaMH(), d.getHocKy(), d.getNamHoc());
    }

    public static void delete(String maSV, String maMH, int hocKy, String namHoc) throws SQLException {
        DBConnection.execute("DELETE FROM DIEM WHERE MaSV=? AND MaMH=? AND HocKy=? AND NamHoc=?",
                maSV, maMH, hocKy, namHoc);
    }
}
