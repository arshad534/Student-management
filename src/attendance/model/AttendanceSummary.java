package attendance.model;

/** Per student, per course attendance totals. */
public class AttendanceSummary {
    public static final double REQUIRED_PERCENT = 75.0;

    private final String rollNo;
    private final String studentName;
    private final String courseCode;
    private final int totalClasses;
    private final int attended;

    public AttendanceSummary(String rollNo, String studentName, String courseCode,
                             int totalClasses, int attended) {
        this.rollNo = rollNo;
        this.studentName = studentName;
        this.courseCode = courseCode;
        this.totalClasses = totalClasses;
        this.attended = attended;
    }

    public String getRollNo() { return rollNo; }
    public String getStudentName() { return studentName; }
    public String getCourseCode() { return courseCode; }
    public int getTotalClasses() { return totalClasses; }
    public int getAttended() { return attended; }

    public double getPercentage() {
        return totalClasses == 0 ? 0.0 : (attended * 100.0) / totalClasses;
    }

    public boolean isShort() {
        return totalClasses > 0 && getPercentage() < REQUIRED_PERCENT;
    }
}
