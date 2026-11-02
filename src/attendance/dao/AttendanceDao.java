package attendance.dao;

import attendance.exception.AttendanceException;
import attendance.model.AttendanceSummary;
import attendance.util.DBConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDao {

    /** Marks attendance after checking enrollment and duplicates. */
    public void mark(int studentId, String courseCode, LocalDate date, boolean present)
            throws AttendanceException {
        try (Connection c = DBConnection.get()) {
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT 1 FROM enrollments WHERE student_id = ? AND course_code = ?")) {
                ps.setInt(1, studentId);
                ps.setString(2, courseCode);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new AttendanceException("Student is not enrolled in " + courseCode + ".");
                    }
                }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT 1 FROM attendance WHERE student_id = ? AND course_code = ? AND att_date = ?")) {
                ps.setInt(1, studentId);
                ps.setString(2, courseCode);
                ps.setDate(3, Date.valueOf(date));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        throw new AttendanceException("Attendance already marked for " + date + ".");
                    }
                }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO attendance (student_id, course_code, att_date, present) VALUES (?,?,?,?)")) {
                ps.setInt(1, studentId);
                ps.setString(2, courseCode);
                ps.setDate(3, Date.valueOf(date));
                ps.setBoolean(4, present);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new AttendanceException("Database error: " + e.getMessage(), e);
        }
    }

    public List<AttendanceSummary> summaries() throws SQLException {
        String sql = "SELECT s.roll_no, s.name, e.course_code, "
                + "COUNT(a.id) AS total, COALESCE(SUM(a.present), 0) AS attended "
                + "FROM enrollments e "
                + "JOIN students s ON s.id = e.student_id "
                + "LEFT JOIN attendance a ON a.student_id = e.student_id AND a.course_code = e.course_code "
                + "GROUP BY s.id, s.roll_no, s.name, e.course_code "
                + "ORDER BY s.roll_no, e.course_code";
        List<AttendanceSummary> list = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new AttendanceSummary(rs.getString("roll_no"), rs.getString("name"),
                        rs.getString("course_code"), rs.getInt("total"), rs.getInt("attended")));
            }
        }
        return list;
    }
}
