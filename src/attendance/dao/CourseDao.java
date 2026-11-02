package attendance.dao;

import attendance.model.Course;
import attendance.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CourseDao implements Dao<Course> {

    @Override
    public void add(Course course) throws SQLException {
        String sql = "INSERT INTO courses (code, title, credits, instructor) VALUES (?,?,?,?)";
        try (Connection c = DBConnection.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, course.getCode());
            ps.setString(2, course.getTitle());
            ps.setInt(3, course.getCredits());
            ps.setString(4, course.getInstructor());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Course> findAll() throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT code, title, credits, instructor FROM courses ORDER BY code";
        try (Connection c = DBConnection.get();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Course(rs.getString("code"), rs.getString("title"),
                        rs.getInt("credits"), rs.getString("instructor")));
            }
        }
        return list;
    }

    public void enroll(int studentId, String courseCode) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, course_code) VALUES (?,?)";
        try (Connection c = DBConnection.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setString(2, courseCode);
            ps.executeUpdate();
        }
    }
    public void delete(String code) throws SQLException
     { try (Connection c = DBConnection.get();
        PreparedStatement ps = c.prepareStatement("DELETE FROM courses WHERE code = ?"))
        { 
            ps.setString(1, code);
            ps.executeUpdate();

        }
     }

}
