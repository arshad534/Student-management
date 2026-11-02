package attendance.util;

import attendance.exception.AttendanceException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/** String-handling helpers for input validation. */
public final class Validator {
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private Validator() { }

    public static String required(String value, String fieldName) throws AttendanceException {
        if (value == null || value.trim().isEmpty()) {
            throw new AttendanceException(fieldName + " must not be empty.");
        }
        return value.trim();
    }

    public static String email(String value) throws AttendanceException {
        String v = required(value, "Email");
        if (!EMAIL.matcher(v).matches()) {
            throw new AttendanceException("Invalid email address: " + v);
        }
        return v.toLowerCase();
    }

    public static int positiveInt(String value, String fieldName) throws AttendanceException {
        try {
            int n = Integer.parseInt(required(value, fieldName));
            if (n <= 0) {
                throw new AttendanceException(fieldName + " must be greater than zero.");
            }
            return n;
        } catch (NumberFormatException e) {
            throw new AttendanceException(fieldName + " must be a whole number.", e);
        }
    }

    public static LocalDate date(String value) throws AttendanceException {
        try {
            return LocalDate.parse(required(value, "Date"));
        } catch (DateTimeParseException e) {
            throw new AttendanceException("Date must be in yyyy-MM-dd format.", e);
        }
    }
}
