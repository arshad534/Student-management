package attendance.ui;

import attendance.dao.CourseDao;
import attendance.dao.StudentDao;
import attendance.exception.AttendanceException;
import attendance.model.Course;
import attendance.model.Student;
import attendance.util.Validator;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class CoursePanel extends JPanel implements Refreshable {
    private final CourseDao courseDao = new CourseDao();
    private final StudentDao studentDao = new StudentDao();

    private final JTextField codeField = new JTextField(7);
    private final JTextField titleField = new JTextField(14);
    private final JTextField creditsField = new JTextField(3);
    private final JTextField instructorField = new JTextField(12);

    private final JComboBox<Student> studentBox = new JComboBox<>();
    private final JComboBox<Course> courseBox = new JComboBox<>();

    private final DefaultTableModel model =
            new DefaultTableModel(new String[] {"Code", "Title", "Credits", "Instructor"}, 0) {
                @Override
                public boolean isCellEditable(int row, int col) { return false; }
            };
    private final JTable table=new JTable(model);

    public CoursePanel() {
        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new GridLayout(2, 1));
        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addRow.add(new JLabel("Code"));
        addRow.add(codeField);
        addRow.add(new JLabel("Title"));
        addRow.add(titleField);
        addRow.add(new JLabel("Credits"));
        addRow.add(creditsField);
        addRow.add(new JLabel("Instructor"));
        addRow.add(instructorField);
        JButton add = new JButton("Add course");
        addRow.add(add);
        JButton delete=new JButton("Delete selected");
        addRow.add(delete);
        delete.addActionListener(e->deleteSelected());

        JPanel enrollRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        enrollRow.add(new JLabel("Enroll"));
        enrollRow.add(studentBox);
        enrollRow.add(new JLabel("in"));
        enrollRow.add(courseBox);
        JButton enroll = new JButton("Enroll");
        enrollRow.add(enroll);

        top.add(addRow);
        top.add(enrollRow);

        add.addActionListener(e -> addCourse());
        enroll.addActionListener(e -> enrollStudent());

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void addCourse() {
        try {
            Course c = new Course(
                    Validator.required(codeField.getText(), "Code").toUpperCase(),
                    Validator.required(titleField.getText(), "Title"),
                    Validator.positiveInt(creditsField.getText(), "Credits"),
                    Validator.required(instructorField.getText(), "Instructor"));
            courseDao.add(c);
            codeField.setText("");
            titleField.setText("");
            creditsField.setText("");
            instructorField.setText("");
            refresh();
        } catch (AttendanceException ex) {
            error(ex.getMessage());
        } catch (SQLException ex) {
            error("Could not save course: " + ex.getMessage());
        }
    }

    private void enrollStudent() {
        Student s = (Student) studentBox.getSelectedItem();
        Course c = (Course) courseBox.getSelectedItem();
        if (s == null || c == null) {
            error("Add at least one student and one course first.");
            return;
        }
        try {
            courseDao.enroll(s.getId(), c.getCode());
            JOptionPane.showMessageDialog(this, s.getName() + " enrolled in " + c.getCode());
        } catch (SQLException ex) {
            error("Could not enroll (already enrolled?): " + ex.getMessage());
        }
    }
    private void deleteSelected()
     {
        int row = table.getSelectedRow();
        if (row < 0)
        {
            error("Select a course first.");
            return;
        }
        String code = (String) model.getValueAt(row, 0);
        int ok = JOptionPane.showConfirmDialog(this,
            "Delete " + code + " and its enrollments and attendance?",
            "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION)
        {
            return;
        }
        try
        {
            courseDao.delete(code);
            refresh();
        } catch (SQLException ex) {
        error("Could not delete: " + ex.getMessage());
    }
  }

    @Override
    public void refresh() {
        try {
            model.setRowCount(0);
            courseBox.removeAllItems();
            studentBox.removeAllItems();
            for (Course c : courseDao.findAll()) {
                model.addRow(new Object[] {c.getCode(), c.getTitle(), c.getCredits(), c.getInstructor()});
                courseBox.addItem(c);
            }
            for (Student s : studentDao.findAll()) {
                studentBox.addItem(s);
            }
        } catch (SQLException ex) {
            error("Could not load data: " + ex.getMessage());
        }
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
