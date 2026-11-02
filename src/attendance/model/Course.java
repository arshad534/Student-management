package attendance.model;

public class Course {
    private final String code;
    private final String title;
    private final int credits;
    private final String instructor;

    public Course(String code, String title, int credits, String instructor) {
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.instructor = instructor;
    }

    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getCredits() { return credits; }
    public String getInstructor() { return instructor; }

    @Override
    public String toString() {
        return code + " - " + title;
    }
}
