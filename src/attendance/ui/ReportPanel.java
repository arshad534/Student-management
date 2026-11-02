package attendance.ui;

import attendance.dao.AttendanceDao;
import attendance.model.AttendanceSummary;
import attendance.util.ReportExporter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ReportPanel extends JPanel implements Refreshable {
    private final AttendanceDao dao = new AttendanceDao();
    private final List<AttendanceSummary> current = new ArrayList<>();
    private final JLabel status = new JLabel(" ");
    private final DefaultTableModel model = new DefaultTableModel(
            new String[] {"Roll No", "Name", "Course", "Total", "Attended", "Percent", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = new JTable(model);

    public ReportPanel() {
        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton reload = new JButton("Reload");
        JButton export = new JButton("Export CSV");
        top.add(reload);
        top.add(export);
        top.add(status);

        reload.addActionListener(e -> refresh());
        export.addActionListener(e -> export());

        // Highlight students below the required attendance percentage.
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                                                           boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, selected, focus, row, col);
                boolean isShort = "SHORT".equals(t.getValueAt(row, 6));
                if (!selected) {
                    c.setBackground(isShort ? new Color(255, 220, 220) : Color.WHITE);
                }
                return c;
            }
        });

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    @Override
    public void refresh() {
        try {
            current.clear();
            current.addAll(dao.summaries());
            model.setRowCount(0);
            for (AttendanceSummary s : current) {
                model.addRow(new Object[] {s.getRollNo(), s.getStudentName(), s.getCourseCode(),
                        s.getTotalClasses(), s.getAttended(), String.format("%.1f%%", s.getPercentage()),
                        s.isShort() ? "SHORT" : "OK"});
            }
            status.setText(current.size() + " row(s). Below "
                    + (int) AttendanceSummary.REQUIRED_PERCENT + "% is highlighted.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not load report: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void export() {
        if (current.isEmpty()) {
            status.setText("Nothing to export.");
            return;
        }
        File target = new File("attendance_report.csv");
        status.setText("Exporting...");
        // Worker thread keeps the GUI responsive; result is posted back on the event thread.
        Thread worker = new Thread(new ReportExporter(new ArrayList<>(current), target,
                msg -> SwingUtilities.invokeLater(() -> status.setText(msg))), "report-exporter");
        worker.start();
    }
}
