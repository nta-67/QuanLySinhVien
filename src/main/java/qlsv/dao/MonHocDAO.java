package qlsv.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import qlsv.db.DBConnection;
import qlsv.model.MonHoc;

public class MonHocDAO {

    public static List<MonHoc> search(String keyword) throws SQLException {
        String sql = "SELECT MaMH, TenMH, SoTinChi, MaKhoa FROM MONHOC "
                + "WHERE MaMH LIKE ? OR TenMH LIKE ? ORDER BY MaMH";
        String k = "%" + keyword.trim() + "%";
        List<MonHoc> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, k);
            ps.setString(2, k);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new MonHoc(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getString(4)));
                }
            }
        }
        return list;
    }

    public static void insert(MonHoc m) throws SQLException {
        DBConnection.execute("INSERT INTO MONHOC(MaMH, TenMH, SoTinChi, MaKhoa) VALUES(?,?,?,?)",
                m.getMaMH(), m.getTenMH(), m.getSoTinChi(), m.getMaKhoa());
    }

    public static void update(MonHoc m) throws SQLException {
        DBConnection.execute("UPDATE MONHOC SET TenMH=?, SoTinChi=?, MaKhoa=? WHERE MaMH=?",
                m.getTenMH(), m.getSoTinChi(), m.getMaKhoa(), m.getMaMH());
    }

    public static void delete(String maMH) throws SQLException {
        DBConnection.execute("DELETE FROM MONHOC WHERE MaMH=?", maMH);
    }
}
