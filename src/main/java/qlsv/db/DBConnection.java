package qlsv.db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Kết nối SQL Server. SỬA USER và PASS bên dưới cho đúng với máy của bạn.
 */
public class DBConnection {

    private static final String HOST = "localhost";
    private static final String PORT = "1433";
    private static final String DATABASE = "QuanLySinhVien";
    private static final String USER = "sa";
    private static final String PASS = "123456";   // <-- đổi thành mật khẩu của bạn

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:sqlserver://" + HOST + ":" + PORT
                + ";databaseName=" + DATABASE
                + ";encrypt=false;trustServerCertificate=true";
        return DriverManager.getConnection(url, USER, PASS);
    }

    /** Chạy INSERT / UPDATE / DELETE với tham số. */
    public static int execute(String sql, Object... params) throws SQLException {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate();
        }
    }

    /** Chuỗi rỗng -> null (để cột UNIQUE như Email không bị trùng chuỗi rỗng). */
    public static String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }

    public static Double getDouble(ResultSet rs, int index) throws SQLException {
        BigDecimal b = rs.getBigDecimal(index);
        return b == null ? null : b.doubleValue();
    }
}
