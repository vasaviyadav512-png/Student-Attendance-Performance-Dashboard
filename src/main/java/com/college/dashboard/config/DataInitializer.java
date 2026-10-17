package com.college.dashboard.config;

import com.college.dashboard.entity.*;
import com.college.dashboard.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarkRepository markRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed-demo-data:true}")
    private boolean seedDemoData;

    public DataInitializer(UserRepository userRepository,
                           StudentRepository studentRepository,
                           SubjectRepository subjectRepository,
                           AttendanceRepository attendanceRepository,
                           MarkRepository markRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.attendanceRepository = attendanceRepository;
        this.markRepository = markRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedDemoData) {
            logger.info("Demo data seeding is disabled (app.seed-demo-data=false)");
            return;
        }

        seedUsers();
        seedSubjects();
        seedStudents();
        seedAttendance();
        seedMarks();

        logger.info("Database initialization completed successfully!");
    }

    private void seedUsers() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    "Dr. Ramesh Sharma (Admin)",
                    "admin@college.edu",
                    Role.ADMIN
            );
            userRepository.save(admin);
            logger.info("Seeded Admin user: admin / admin123");
        }

        if (!userRepository.existsByUsername("teacher")) {
            User teacher = new User(
                    "teacher",
                    passwordEncoder.encode("teacher123"),
                    "Prof. Sunita Verma",
                    "teacher@college.edu",
                    Role.TEACHER
            );
            userRepository.save(teacher);
            logger.info("Seeded Teacher user: teacher / teacher123");
        }
    }

    private void seedSubjects() {
        if (subjectRepository.count() == 0) {
            List<Subject> subjects = List.of(
                    new Subject("CS301", "Data Structures & Algorithms", "Computer Science & Engineering", 3, 4),
                    new Subject("CS302", "Database Management Systems", "Computer Science & Engineering", 3, 4),
                    new Subject("CS303", "Operating Systems", "Computer Science & Engineering", 4, 3),
                    new Subject("CS304", "Computer Networks", "Computer Science & Engineering", 4, 3),
                    new Subject("CS305", "Software Engineering", "Computer Science & Engineering", 5, 3)
            );
            subjectRepository.saveAll(subjects);
            logger.info("Seeded {} academic subjects", subjects.size());
        }
    }

    private void seedStudents() {
        if (studentRepository.count() == 0) {
            List<Student> students = List.of(
                    new Student("CSE2024001", "Aarav Patel", "aarav.patel@college.edu", "Computer Science & Engineering", 3, "A"),
                    new Student("CSE2024002", "Ananya Sharma", "ananya.sharma@college.edu", "Computer Science & Engineering", 3, "A"),
                    new Student("CSE2024003", "Rohan Gupta", "rohan.gupta@college.edu", "Computer Science & Engineering", 3, "A"),
                    new Student("CSE2024004", "Pooja Iyer", "pooja.iyer@college.edu", "Computer Science & Engineering", 3, "B"),
                    new Student("CSE2024005", "Karthik Reddy", "karthik.reddy@college.edu", "Computer Science & Engineering", 3, "B"),
                    new Student("CSE2024006", "Sneha Kulkarni", "sneha.kulkarni@college.edu", "Computer Science & Engineering", 2, "A"),
                    new Student("CSE2024007", "Vikram Malhotra", "vikram.malhotra@college.edu", "Computer Science & Engineering", 2, "A"),
                    new Student("CSE2024008", "Divya Nair", "divya.nair@college.edu", "Computer Science & Engineering", 2, "B"),
                    new Student("CSE2024009", "Aditya Joshi", "aditya.joshi@college.edu", "Computer Science & Engineering", 4, "A"),
                    new Student("CSE2024010", "Meera Deshmukh", "meera.deshmukh@college.edu", "Computer Science & Engineering", 4, "B")
            );
            studentRepository.saveAll(students);
            logger.info("Seeded {} student records", students.size());
        }
    }

    private void seedAttendance() {
        if (attendanceRepository.count() == 0) {
            List<Student> students = studentRepository.findAll();
            List<Subject> subjects = subjectRepository.findAll();

            if (students.isEmpty() || subjects.isEmpty()) return;

            Subject ds = subjects.get(0); // CS301
            Subject dbms = subjects.size() > 1 ? subjects.get(1) : ds;

            List<Attendance> records = new ArrayList<>();
            LocalDate baseDate = LocalDate.now().minusDays(10);

            for (Student student : students) {
                for (int i = 0; i < 5; i++) {
                    LocalDate date = baseDate.plusDays(i);

                    // Rohan Gupta (student index 2) has poor attendance (4 out of 5 absent)
                    // Pooja Iyer (student index 3) has poor attendance (3 out of 5 absent)
                    AttendanceStatus status;
                    String remarks = "Present in class";

                    if (student.getStudentId().equals("CSE2024003")) {
                        status = (i == 2) ? AttendanceStatus.PRESENT : AttendanceStatus.ABSENT;
                        remarks = (status == AttendanceStatus.ABSENT) ? "Unexcused absence" : "Present";
                    } else if (student.getStudentId().equals("CSE2024004")) {
                        status = (i % 2 == 0) ? AttendanceStatus.PRESENT : AttendanceStatus.ABSENT;
                        remarks = (status == AttendanceStatus.ABSENT) ? "Medical leave" : "Present";
                    } else {
                        status = (i == 4 && student.getStudentId().equals("CSE2024005")) ? AttendanceStatus.ABSENT : AttendanceStatus.PRESENT;
                        remarks = (status == AttendanceStatus.ABSENT) ? "Sports event" : "On time";
                    }

                    records.add(new Attendance(student, ds, date, status, remarks));
                }
            }

            attendanceRepository.saveAll(records);
            logger.info("Seeded {} attendance records", records.size());
        }
    }

    private void seedMarks() {
        if (markRepository.count() == 0) {
            List<Student> students = studentRepository.findAll();
            List<Subject> subjects = subjectRepository.findAll();

            if (students.isEmpty() || subjects.isEmpty()) return;

            Subject ds = subjects.get(0);
            Subject dbms = subjects.size() > 1 ? subjects.get(1) : ds;

            List<Mark> marks = new ArrayList<>();

            for (Student student : students) {
                // Configure varied performance profiles
                double midtermMarks;
                double finalMarks;
                String remarks = "Good effort";

                if (student.getStudentId().equals("CSE2024001") || student.getStudentId().equals("CSE2024002")) {
                    // Top performers
                    midtermMarks = 92.0;
                    finalMarks = 89.0;
                    remarks = "Excellent analytical skills";
                } else if (student.getStudentId().equals("CSE2024003")) {
                    // Low performer (Academic Risk)
                    midtermMarks = 34.0;
                    finalMarks = 38.0;
                    remarks = "Needs intensive remedial classes";
                } else if (student.getStudentId().equals("CSE2024004")) {
                    // Average
                    midtermMarks = 68.0;
                    finalMarks = 71.0;
                    remarks = "Satisfactory grasp of fundamentals";
                } else {
                    // Good
                    midtermMarks = 78.0;
                    finalMarks = 82.0;
                    remarks = "Consistent performance";
                }

                String gradeMid = Mark.calculateGrade(midtermMarks, 100.0);
                String gradeFin = Mark.calculateGrade(finalMarks, 100.0);

                marks.add(new Mark(student, ds, ExamType.MIDTERM, midtermMarks, 100.0, gradeMid, remarks));
                marks.add(new Mark(student, ds, ExamType.FINAL, finalMarks, 100.0, gradeFin, remarks));

                // Add DBMS midterm marks
                double dbmsMarks = Math.max(30.0, Math.min(95.0, midtermMarks - 4.0));
                marks.add(new Mark(student, dbms, ExamType.MIDTERM, dbmsMarks, 100.0, Mark.calculateGrade(dbmsMarks, 100.0), remarks));
            }

            markRepository.saveAll(marks);
            logger.info("Seeded {} exam marks records", marks.size());
        }
    }
}
