package attendance.ui;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class MainFrame extends JFrame {

    public MainFrame() {
        super("Student Course & Attendance Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Students", new StudentPanel());
        tabs.addTab("Courses", new CoursePanel());
        tabs.addTab("Attendance", new AttendancePanel());
        tabs.addTab("Report", new ReportPanel());

        // Reload the visible tab whenever the user switches tabs.
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedComponent() instanceof Refreshable) {
                ((Refreshable) tabs.getSelectedComponent()).refresh();
            }
        });
        ((Refreshable) tabs.getSelectedComponent()).refresh();

        add(tabs);
        setPreferredSize(new Dimension(900, 520));
        pack();
        setLocationRelativeTo(null);
    }
}
