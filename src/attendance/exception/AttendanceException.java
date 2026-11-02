package attendance.exception;

/** Checked exception for business-rule violations (not enrolled, duplicate entry, bad input). */
public class AttendanceException extends Exception {
    public AttendanceException(String message) {
        super(message);
    }

    public AttendanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
