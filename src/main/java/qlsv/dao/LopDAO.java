package qlsv.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import qlsv.db.DBConnection;
import qlsv.model.Lop;

public class LopDAO {

    public static List<Lop> search(String keyword) throws SQLException {
        String sql = "SELECT MaLop, TenLop, KhoaHoc, MaKhoa FROM LOP "
                + "WHERE MaLop LIKE ? OR TenLop LIKE ? ORDER BY MaLop";
        String k = "%" + keyword.trim() + "%";
        List<Lop> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, k);
            ps.setString(2, k);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Lop(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));
                }
            }
        }
        return list;
    }

    public static void insert(Lop l) throws SQLException {
        DBConnection.execute("INSERT INTO LOP(MaLop, TenLop, KhoaHoc, MaKhoa) VALUES(?,?,?,?)",
                l.getMaLop(), l.getTenLop(), DBConnection.emptyToNull(l.getKhoaHoc()), l.getMaKhoa());
    }

    public static void update(Lop l) throws SQLException {
        DBConnection.execute("UPDATE LOP SET TenLop=?, KhoaHoc=?, MaKhoa=? WHERE MaLop=?",
                l.getTenLop(), DBConnection.emptyToNull(l.getKhoaHoc()), l.getMaKhoa(), l.getMaLop());
    }

    public static void delete(String maLop) throws SQLException {
        DBConnection.execute("DELETE FROM LOP WHERE MaLop=?", maLop);
    }
}
