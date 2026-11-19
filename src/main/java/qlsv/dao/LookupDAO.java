package qlsv.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import qlsv.db.DBConnection;
import qlsv.model.Item;

/** Lấy danh sách (mã, tên) để đổ vào ComboBox. */
public class LookupDAO {

    public static List<Item> khoa() throws SQLException {
        return query("SELECT MaKhoa, TenKhoa FROM KHOA ORDER BY MaKhoa");
    }

    public static List<Item> lop() throws SQLException {
        return query("SELECT MaLop, TenLop FROM LOP ORDER BY MaLop");
    }

    public static List<Item> sinhVien() throws SQLException {
        return query("SELECT MaSV, HoTen FROM SINHVIEN ORDER BY MaSV");
    }

    public static List<Item> monHoc() throws SQLException {
        return query("SELECT MaMH, TenMH FROM MONHOC ORDER BY MaMH");
    }

    private static List<Item> query(String sql) throws SQLException {
        List<Item> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Item(rs.getString(1), rs.getString(2)));
            }
        }
        return list;
    }
}
