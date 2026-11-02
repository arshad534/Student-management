package attendance.util;

import attendance.model.AttendanceSummary;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/** Writes the attendance report to CSV on a background thread (multithreading + file I/O). */
public class ReportExporter implements Runnable {
    private final List<AttendanceSummary> rows;
    private final File target;
    private final Consumer<String> onDone;

    public ReportExporter(List<AttendanceSummary> rows, File target, Consumer<String> onDone) {
        this.rows = rows;
        this.target = target;
        this.onDone = onDone;
    }

    @Override
    public void run() {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(target))) {
            out.write("RollNo,Name,Course,Total,Attended,Percent,Status");
            out.newLine();
            for (AttendanceSummary s : rows) {
                out.write(String.format("%s,\"%s\",%s,%d,%d,%.1f,%s",
                        s.getRollNo(), s.getStudentName().replace("\"", "\"\""), s.getCourseCode(),
                        s.getTotalClasses(), s.getAttended(), s.getPercentage(),
                        s.isShort() ? "SHORT" : "OK"));
                out.newLine();
            }
            onDone.accept("Report saved to " + target.getAbsolutePath());
        } catch (IOException e) {
            onDone.accept("Export failed: " + e.getMessage());
        }
    }
}
