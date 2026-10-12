package ma.uir.awd.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import ma.uir.awd.model.AttendanceRecord;

public class AttendanceDaoJdbc implements IAttendanceDao {

    private static final AttendanceDaoJdbc INSTANCE = new AttendanceDaoJdbc();

    private AttendanceDaoJdbc() { }

    public static AttendanceDaoJdbc getInstance() {
        return INSTANCE;
    }

    @Override
    public void record(String studentEmail, String labTitle,
                       LocalDate sessionDate, boolean present) {
        String sql =
                "INSERT INTO attendance (student_id, lab_title, session_date, present) "
                        + "SELECT id, ?, ?, ? FROM members WHERE email = ? AND role = 'student' "
                        + "ON DUPLICATE KEY UPDATE present = VALUES(present)";
        try {
            Connection c = SingletonConnection.getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, labTitle);
                ps.setDate(2, Date.valueOf(sessionDate));
                ps.setBoolean(3, present);
                ps.setString(4, studentEmail);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AttendanceRecord> findByStudent(String studentEmail) {
        String sql =
                "SELECT a.lab_title, a.session_date, a.present "
                        + "FROM attendance a "
                        + "JOIN members m ON m.id = a.student_id "
                        + "WHERE m.email = ? "
                        + "ORDER BY a.session_date DESC";
        List<AttendanceRecord> result = new ArrayList<>();
        try {
            Connection c = SingletonConnection.getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, studentEmail);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(new AttendanceRecord(
                                rs.getString("lab_title"),
                                rs.getDate("session_date").toLocalDate(),
                                rs.getBoolean("present")));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public double attendanceRate(String studentEmail) {
        String sql =
                "SELECT COALESCE(AVG(a.present), 0) * 100 AS rate "
                        + "FROM attendance a "
                        + "JOIN members m ON m.id = a.student_id "
                        + "WHERE m.email = ?";
        try {
            Connection c = SingletonConnection.getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, studentEmail);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getDouble("rate");
                    }
                    return 0.0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}