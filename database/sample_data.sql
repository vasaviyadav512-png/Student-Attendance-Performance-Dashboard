-- =========================================================
-- Sample Data Script: Student Attendance & Performance Dashboard
-- =========================================================

USE student_dashboard;

-- 1. Insert Sample Users
-- Password for both users is encrypted via BCrypt (e.g. admin123 and teacher123)
-- When starting Spring Boot, DataInitializer will also auto-seed them if empty!
INSERT INTO users (username, password, full_name, email, role) VALUES
('admin', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxGQacXYQV2C', 'Dr. Ramesh Sharma (Admin)', 'admin@college.edu', 'ADMIN'),
('teacher', '$2a$10$wTqS8lXk8vK05b4aB0V/4e7hY8oQ5h1rJg2i8h8a6L2K1J0H9G8F7', 'Prof. Sunita Verma', 'teacher@college.edu', 'TEACHER')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

-- 2. Insert Subjects
INSERT INTO subjects (subject_code, subject_name, department, semester, credits) VALUES
('CS301', 'Data Structures & Algorithms', 'Computer Science & Engineering', 3, 4),
('CS302', 'Database Management Systems', 'Computer Science & Engineering', 3, 4),
('CS303', 'Operating Systems', 'Computer Science & Engineering', 4, 3),
('CS304', 'Computer Networks', 'Computer Science & Engineering', 4, 3),
('CS305', 'Software Engineering', 'Computer Science & Engineering', 5, 3)
ON DUPLICATE KEY UPDATE subject_name=VALUES(subject_name);

-- 3. Insert Students (Mix of high performers, average students, and students at risk)
INSERT INTO students (student_id, name, email, department, year, section) VALUES
('CSE2024001', 'Aarav Patel', 'aarav.patel@college.edu', 'Computer Science & Engineering', 3, 'A'),
('CSE2024002', 'Ananya Sharma', 'ananya.sharma@college.edu', 'Computer Science & Engineering', 3, 'A'),
('CSE2024003', 'Rohan Gupta', 'rohan.gupta@college.edu', 'Computer Science & Engineering', 3, 'A'),
('CSE2024004', 'Pooja Iyer', 'pooja.iyer@college.edu', 'Computer Science & Engineering', 3, 'B'),
('CSE2024005', 'Karthik Reddy', 'karthik.reddy@college.edu', 'Computer Science & Engineering', 3, 'B'),
('CSE2024006', 'Sneha Kulkarni', 'sneha.kulkarni@college.edu', 'Computer Science & Engineering', 2, 'A'),
('CSE2024007', 'Vikram Malhotra', 'vikram.malhotra@college.edu', 'Computer Science & Engineering', 2, 'A'),
('CSE2024008', 'Divya Nair', 'divya.nair@college.edu', 'Computer Science & Engineering', 2, 'B'),
('CSE2024009', 'Aditya Joshi', 'aditya.joshi@college.edu', 'Computer Science & Engineering', 4, 'A'),
('CSE2024010', 'Meera Deshmukh', 'meera.deshmukh@college.edu', 'Computer Science & Engineering', 4, 'B')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 4. Insert Attendance Records (Demonstrating varying attendance percentages)
-- CS301 (Subject ID: 1)
INSERT INTO attendance (student_id, subject_id, attendance_date, status, remarks) VALUES
-- Aarav Patel (Good attendance)
(1, 1, '2026-10-01', 'PRESENT', 'On time'),
(1, 1, '2026-10-02', 'PRESENT', 'On time'),
(1, 1, '2026-10-03', 'PRESENT', 'On time'),
(1, 1, '2026-10-04', 'PRESENT', 'On time'),
(1, 1, '2026-10-05', 'PRESENT', 'On time'),
-- Ananya Sharma (Good attendance)
(2, 1, '2026-10-01', 'PRESENT', 'Active participant'),
(2, 1, '2026-10-02', 'PRESENT', 'On time'),
(2, 1, '2026-10-03', 'PRESENT', 'On time'),
(2, 1, '2026-10-04', 'PRESENT', 'On time'),
(2, 1, '2026-10-05', 'PRESENT', 'On time'),
-- Rohan Gupta (At Risk: < 75% attendance)
(3, 1, '2026-10-01', 'ABSENT', 'Unexcused absence'),
(3, 1, '2026-10-02', 'ABSENT', 'Sick leave reported late'),
(3, 1, '2026-10-03', 'PRESENT', 'Present'),
(3, 1, '2026-10-04', 'ABSENT', 'Unexcused absence'),
(3, 1, '2026-10-05', 'PRESENT', 'Present'),
-- Pooja Iyer (At Risk: < 75% attendance)
(4, 1, '2026-10-01', 'PRESENT', 'Present'),
(4, 1, '2026-10-02', 'ABSENT', 'Medical leave'),
(4, 1, '2026-10-03', 'ABSENT', 'Medical leave'),
(4, 1, '2026-10-04', 'ABSENT', 'Unexcused'),
(4, 1, '2026-10-05', 'PRESENT', 'Present'),
-- Karthik Reddy (Moderate attendance)
(5, 1, '2026-10-01', 'PRESENT', 'Present'),
(5, 1, '2026-10-02', 'PRESENT', 'Present'),
(5, 1, '2026-10-03', 'ABSENT', 'Sports tournament'),
(5, 1, '2026-10-04', 'PRESENT', 'Present'),
(5, 1, '2026-10-05', 'PRESENT', 'Present')
ON DUPLICATE KEY UPDATE status=VALUES(status);

-- 5. Insert Academic Marks
-- CS301 (Subject 1) and CS302 (Subject 2)
INSERT INTO marks (student_id, subject_id, exam_type, marks_obtained, max_marks, grade, remarks) VALUES
-- Aarav Patel (Top student)
(1, 1, 'MIDTERM', 92.0, 100.0, 'A+', 'Outstanding performance'),
(1, 1, 'FINAL', 88.5, 100.0, 'A', 'Excellent code design'),
(1, 2, 'MIDTERM', 85.0, 100.0, 'A', 'Strong query optimization skills'),
-- Ananya Sharma (High achiever)
(2, 1, 'MIDTERM', 95.0, 100.0, 'A+', 'Top score in cohort'),
(2, 1, 'FINAL', 91.0, 100.0, 'A+', 'Flawless recursion proofs'),
(2, 2, 'MIDTERM', 89.0, 100.0, 'A', 'Great normalization grasp'),
-- Rohan Gupta (Needs improvement: low marks)
(3, 1, 'MIDTERM', 34.0, 100.0, 'F', 'Failed to implement graph traversal'),
(3, 1, 'FINAL', 42.0, 100.0, 'D', 'Barely passed final paper'),
(3, 2, 'MIDTERM', 38.0, 100.0, 'F', 'Needs revision on SQL Joins'),
-- Pooja Iyer (Average student)
(4, 1, 'MIDTERM', 68.0, 100.0, 'B', 'Satisfactory analysis'),
(4, 1, 'FINAL', 72.0, 100.0, 'B+', 'Good effort on dynamic programming'),
(4, 2, 'MIDTERM', 65.0, 100.0, 'B', 'Good indexing answers'),
-- Karthik Reddy (Good student)
(5, 1, 'MIDTERM', 78.0, 100.0, 'B+', 'Very solid practical work'),
(5, 1, 'FINAL', 81.0, 100.0, 'A', 'Great work on binary heaps'),
(5, 2, 'MIDTERM', 76.0, 100.0, 'B+', 'Good conceptual understanding')
ON DUPLICATE KEY UPDATE marks_obtained=VALUES(marks_obtained);
