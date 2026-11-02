package attendance.model;

/** Abstract base class: demonstrates data hiding, inheritance and abstraction. */
public abstract class Person {
    private int id;
    private String name;
    private String email;

    protected Person(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }

    /** Overridden by subclasses (runtime polymorphism). */
    public abstract String getRole();

    @Override
    public String toString() {
        return getRole() + "[" + name + "]";
    }
}
