package attendance.ui;

import attendance.dao.StudentDao;
import attendance.exception.AttendanceException;
import attendance.model.Student;
import attendance.util.Validator;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class StudentPanel extends JPanel implements Refreshable {
    private final StudentDao dao = new StudentDao();
    private final JTextField rollField = new JTextField(8);
    private final JTextField nameField = new JTextField(12);
    private final JTextField emailField = new JTextField(14);
    private final JTextField deptField = new JTextField(10);
    private final DefaultTableModel model =
            new DefaultTableModel(new String[] {"ID", "Roll No", "Name", "Email", "Department"}, 0) {
                @Override
                public boolean isCellEditable(int row, int col) { return false; }
            };
    private final JTable table = new JTable(model);

    public StudentPanel() {
        setLayout(new BorderLayout(8, 8));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Roll No"));
        form.add(rollField);
        form.add(new JLabel("Name"));
        form.add(nameField);
        form.add(new JLabel("Email"));
        form.add(emailField);
        form.add(new JLabel("Dept"));
        form.add(deptField);
        JButton add = new JButton("Add");
        JButton delete = new JButton("Delete selected");
        form.add(add);
        form.add(delete);

        add.addActionListener(e -> addStudent());
        delete.addActionListener(e -> deleteSelected());

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void addStudent() {
        try {
            Student s = new Student(
                    Validator.required(rollField.getText(), "Roll No"),
                    Validator.required(nameField.getText(), "Name"),
                    Validator.email(emailField.getText()),
                    Validator.required(deptField.getText(), "Department"));
            dao.add(s);
            rollField.setText("");
            nameField.setText("");
            emailField.setText("");
            deptField.setText("");
            refresh();
        } catch (AttendanceException ex) {
            error(ex.getMessage());
        } catch (SQLException ex) {
            error("Could not save student: " + ex.getMessage());
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            error("Select a student first.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Delete this student and their records?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            dao.delete((Integer) model.getValueAt(row, 0));
            refresh();
        } catch (SQLException ex) {
            error("Could not delete: " + ex.getMessage());
        }
    }

    @Override
    public void refresh() {
        try {
            model.setRowCount(0);
            for (Student s : dao.findAll()) {
                model.addRow(new Object[] {s.getId(), s.getRollNo(), s.getName(), s.getEmail(),
                        s.getDepartment()});
            }
        } catch (SQLException ex) {
            error("Could not load students: " + ex.getMessage());
        }
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
