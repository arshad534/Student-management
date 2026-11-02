# Student Course & Attendance Management System

A Java Swing + JDBC (MySQL) desktop application for managing students, courses,
enrollments and daily attendance, with a percentage report that flags students
below 75% and exports to CSV.

## Setup

1. Install MySQL and run the schema:
   ```
   mysql -u root -p < schema.sql
   ```
2. Download the MySQL Connector/J JAR (e.g. `mysql-connector-j-8.x.jar`) and put it in this folder.
3. Set credentials if they differ from the defaults (`root`, empty password, `localhost:3306`):
   ```
   export DB_USER=root
   export DB_PASS=yourpassword
   # optional: export DB_URL="jdbc:mysql://localhost:3306/attendance_db?useSSL=false"
   ```
4. Compile and run (Linux/macOS; on Windows use `;` instead of `:` in the classpath):
   ```
   mkdir -p out
   javac -d out $(find src -name '*.java')
   java -cp "out:mysql-connector-j-8.x.jar" attendance.Main
   ```

## How to use

1. **Students** tab: add students (email is validated), delete selected.
2. **Courses** tab: add courses, then enroll a student in a course.
3. **Attendance** tab: pick student, course, date, present/absent, then mark.
   Marking fails with a clear message if the student is not enrolled or the date is already recorded.
4. **Report** tab: per student per course totals and percentage. Rows below 75% are shaded red.
   "Export CSV" writes `attendance_report.csv` on a background thread.

## Package layout

| Package | Contents |
|---|---|
| `attendance.model` | `Person` (abstract), `Student`, `Course`, `AttendanceSummary` |
| `attendance.exception` | `AttendanceException` (checked) |
| `attendance.dao` | `Dao<T>` interface, `StudentDao`, `CourseDao`, `AttendanceDao` (JDBC) |
| `attendance.util` | `DBConnection`, `Validator`, `ReportExporter` |
| `attendance.ui` | `MainFrame`, four tab panels, `Refreshable` interface |

## Course outcome mapping

| CO | Where it appears |
|---|---|
| CO1 Java features and architecture | Platform independence via JVM bytecode, packages, `main` entry point in `Main.java`, JDBC driver loaded at runtime |
| CO2 UML design | Class relationships to draw: `Student` is-a `Person`; `StudentDao`, `CourseDao` implement `Dao<T>`; `AttendanceDao` uses `AttendanceSummary`; panels implement `Refreshable`; Student 1..* Enrollment *..1 Course |
| CO3 OOP | Encapsulation (private fields + getters/setters in `Person`), constructors and overloading (`Student`), inheritance (`Student extends Person`), polymorphism (`getRole()` override, `toString()`) |
| CO4 Packages, interfaces, I/O, strings | Five packages; `Dao<T>` and `Refreshable` interfaces; `BufferedWriter`/`FileWriter` CSV export in `ReportExporter`; regex, `trim`, `String.format` in `Validator` |
| CO5 Exceptions, threads, JDBC | Custom `AttendanceException`, try-with-resources; export runs on its own `Thread`; PreparedStatement CRUD and a JOIN/GROUP BY report in the DAOs |
| CO6 Event handling and Swing | `ActionListener` lambdas on buttons, `ChangeListener` on tabs, `JTabbedPane`, `JTable`, `JComboBox`, custom cell renderer |

## Notes

- The code compiles with a standard JDK (tested with `javac`). Running needs a live MySQL server and the Connector/J JAR, so I could not exercise the GUI against a database here.
- Deleting a student cascades to their enrollments and attendance records.
