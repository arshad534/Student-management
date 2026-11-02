CREATE DATABASE IF NOT EXISTS attendance_db;
USE attendance_db;

CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    roll_no VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(80) NOT NULL,
    email VARCHAR(100) NOT NULL,
    department VARCHAR(60) NOT NULL
);

CREATE TABLE IF NOT EXISTS courses (
    code VARCHAR(10) PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    credits INT NOT NULL,
    instructor VARCHAR(80) NOT NULL
);

CREATE TABLE IF NOT EXISTS enrollments (
    student_id INT NOT NULL,
    course_code VARCHAR(10) NOT NULL,
    PRIMARY KEY (student_id, course_code),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (course_code) REFERENCES courses(code) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS attendance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_code VARCHAR(10) NOT NULL,
    att_date DATE NOT NULL,
    present BOOLEAN NOT NULL,
    UNIQUE (student_id, course_code, att_date),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (course_code) REFERENCES courses(code) ON DELETE CASCADE
);
