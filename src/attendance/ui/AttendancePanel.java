package attendance.ui;

import attendance.dao.AttendanceDao;
import attendance.dao.CourseDao;
import attendance.dao.StudentDao;
import attendance.exception.AttendanceException;
import attendance.model.Course;
import attendance.model.Student;
import attendance.util.Validator;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AttendancePanel extends JPanel implements Refreshable {
    private final AttendanceDao attendanceDao = new AttendanceDao();
    private final CourseDao courseDao = new CourseDao();
    private final StudentDao studentDao = new StudentDao();

    private final JComboBox<Student> studentBox = new JComboBox<>();
    private final JComboBox<Course> courseBox = new JComboBox<>();
    private final JTextField dateField = new JTextField(LocalDate.now().toString(), 9);
    private final JCheckBox presentBox = new JCheckBox("Present", true);
    private final JLabel status = new JLabel(" ");

    public AttendancePanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(new JLabel("Student"));
        add(studentBox);
        add(new JLabel("Course"));
        add(courseBox);
        add(new JLabel("Date (yyyy-MM-dd)"));
        add(dateField);
        add(presentBox);
        JButton mark = new JButton("Mark attendance");
        add(mark);
        add(status);

        mark.addActionListener(e -> mark());
    }

    private void mark() {
        Student s = (Student) studentBox.getSelectedItem();
        Course c = (Course) courseBox.getSelectedItem();
        if (s == null || c == null) {
            JOptionPane.showMessageDialog(this, "Add students and courses first.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            LocalDate date = Validator.date(dateField.getText());
            attendanceDao.mark(s.getId(), c.getCode(), date, presentBox.isSelected());
            status.setText("Saved: " + s.getName() + " / " + c.getCode() + " / " + date
                    + (presentBox.isSelected() ? " present" : " absent"));
        } catch (AttendanceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot mark attendance",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void refresh() {
        try {
            studentBox.removeAllItems();
            courseBox.removeAllItems();
            for (Student s : studentDao.findAll()) {
                studentBox.addItem(s);
            }
            for (Course c : courseDao.findAll()) {
                courseBox.addItem(c);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not load data: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
