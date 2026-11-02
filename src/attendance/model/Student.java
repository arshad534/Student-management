package attendance.model;

/** Student extends Person: constructor overloading and method overriding. */
public class Student extends Person {
    private String rollNo;
    private String department;

    public Student(int id, String rollNo, String name, String email, String department) {
        super(id, name, email);
        this.rollNo = rollNo;
        this.department = department;
    }

    /** Overloaded constructor for a student not yet saved (no id). */
    public Student(String rollNo, String name, String email, String department) {
        this(0, rollNo, name, email, department);
    }

    public String getRollNo() { return rollNo; }
    public String getDepartment() { return department; }

    @Override
    public String getRole() { return "Student"; }

    /** Shown in combo boxes. */
    @Override
    public String toString() {
        return rollNo + " - " + getName();
    }
}
