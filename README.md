# 🎓 Student Attendance & Performance Dashboard

A full-stack, enterprise-grade web application built with **Java 21**, **Spring Boot 3**, **Spring Data JPA / Hibernate**, **MySQL**, **Spring Security (JWT)**, **Bootstrap 5**, and **Chart.js**.

---

## 📌 Features

### 1. 📊 Admin & Teacher Dashboard
- **KPI Summary Cards**: Real-time counters for Total Students, Average Attendance %, Average Academic Marks %, and Students at Risk.
- **Interactive Visualizations (Chart.js)**:
  - Attendance Overview Doughnut Chart (Present vs Absent breakdown).
  - Subject Performance Analysis Bar Chart (Subject-wise average marks).
- **Recent Attendance Feed**: Live table showing the latest 10 attendance records.
- **Students at Risk Alert Panel**: Immediate visibility into students with `< 75%` attendance or failing scores (`< 40%`).

### 2. 👨‍🎓 Student Management
- Complete CRUD operations: Add, View, Edit, Search, and Delete students.
- Live search bar and Department filters.
- Prevents duplicate Student IDs (roll numbers) and duplicate email addresses with custom validation and database unique constraints.

### 3. 📅 Attendance Management
- **Roll Call System**: Select a Course/Subject and Date, toggle Present/Absent for each student, and record attendance with optional remarks in a single click.
- **Quick Actions**: "All Present" and "All Absent" buttons for rapid classroom roll call.
- **Attendance History**: Filter logs by Student, Subject, and Date Range.
- **Automatic Risk Highlighting**: Any student whose cumulative attendance drops below `75%` is clearly flagged with a warning badge.

### 4. 📈 Academic Performance & Marks
- Add and update exam marks for different assessment categories:
  - `MIDTERM` (Midterm Exams)
  - `FINAL` (Semester Final Exams)
  - `QUIZ` (Quizzes)
  - `ASSIGNMENT` (Labs & Assignments)
- **Live Grade Calculation**: Grade (`A+`, `A`, `B+`, `B`, `C`, `D`, `F`) and percentage update live in the UI modal as you type marks.
- Validation prevents entering marks exceeding the maximum marks.

### 5. 🖨️ Official Performance Transcript & Printable Report
- Student-wise academic report card generation.
- Displays student details, attendance metrics, subject-wise scores, and cumulative academic standing (`Distinction`, `First Class`, `Second Class`, `Pass`, `Remedial Required`).
- **One-Click Printable Format**: Dedicated `@media print` styling formats the page cleanly for A4 physical printing or "Save as PDF" with college header and signature blocks.

### 6. 🔐 Secure Authentication & Role-Based Access
- Spring Security with stateless **JWT (JSON Web Tokens)**.
- Passwords encrypted with **BCryptPasswordEncoder** (never stored in plain-text).
- Roles: `ADMIN` and `TEACHER`.
- Automatic demo account seeding on application startup.

---

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| **Backend** | Java 21, Spring Boot 3.3.4 |
| **Security** | Spring Security 6, JJWT 0.12.6, BCrypt |
| **ORM / Database** | Spring Data JPA, Hibernate, MySQL 8.0+ |
| **Build Tool** | Apache Maven |
| **Frontend** | HTML5, CSS3, JavaScript (ES6+), Bootstrap 5.3, Bootstrap Icons |
| **Charts** | Chart.js 4.4 |
| **IDE** | Visual Studio Code (or IntelliJ IDEA / Eclipse) |

---

## 📂 Project Structure

```text
Student Attendance & Performance Dashboard/
├── database/
│   ├── schema.sql                 # Complete MySQL schema creation script
│   └── sample_data.sql            # Sample students, subjects, attendance & marks
├── frontend/                      # Standalone copy of frontend files
│   ├── css/styles.css
│   ├── js/api.js
│   ├── js/app.js
│   └── index.html
├── src/
│   ├── main/
│   │   ├── java/com/college/dashboard/
│   │   │   ├── StudentDashboardApplication.java  # Spring Boot Main Application
│   │   │   ├── config/
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   ├── DataInitializer.java          # Automatic demo data seeder
│   │   │   │   ├── JwtAuthenticationFilter.java  # Intercepts Bearer tokens
│   │   │   │   ├── JwtUtils.java                 # Generates & validates JWTs
│   │   │   │   ├── SecurityConfig.java           # Spring Security filter chain
│   │   │   │   └── WebMvcConfig.java             # CORS & static resource mapping
│   │   │   ├── controller/
│   │   │   │   ├── AttendanceController.java
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── DashboardController.java
│   │   │   │   ├── MarkController.java
│   │   │   │   ├── ReportController.java
│   │   │   │   ├── StudentController.java
│   │   │   │   └── SubjectController.java
│   │   │   ├── dto/
│   │   │   │   ├── ApiResponse.java
│   │   │   │   ├── AttendanceRecordDto.java
│   │   │   │   ├── AttendanceResponseDto.java
│   │   │   │   ├── BatchAttendanceRequest.java
│   │   │   │   ├── DashboardStatsDto.java
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── LoginResponse.java
│   │   │   │   ├── MarkRequestDto.java
│   │   │   │   ├── MarkResponseDto.java
│   │   │   │   ├── StudentDto.java
│   │   │   │   ├── StudentReportDto.java
│   │   │   │   └── SubjectDto.java
│   │   │   ├── entity/
│   │   │   │   ├── Attendance.java
│   │   │   │   ├── AttendanceStatus.java         # Enum: PRESENT, ABSENT
│   │   │   │   ├── ExamType.java                 # Enum: MIDTERM, FINAL, QUIZ, ASSIGNMENT
│   │   │   │   ├── Mark.java
│   │   │   │   ├── Role.java                     # Enum: ADMIN, TEACHER
│   │   │   │   ├── Student.java
│   │   │   │   ├── Subject.java
│   │   │   │   └── User.java
│   │   │   ├── exception/
│   │   │   │   ├── BadRequestException.java
│   │   │   │   ├── DuplicateResourceException.java
│   │   │   │   ├── ErrorResponse.java
│   │   │   │   ├── GlobalExceptionHandler.java   # @RestControllerAdvice
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── repository/
│   │   │   │   ├── AttendanceRepository.java
│   │   │   │   ├── MarkRepository.java
│   │   │   │   ├── StudentRepository.java
│   │   │   │   ├── SubjectRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   └── service/
│   │   │       ├── AttendanceService.java
│   │   │       ├── AuthService.java
│   │   │       ├── DashboardService.java
│   │   │       ├── MarkService.java
│   │   │       ├── ReportService.java
│   │   │       ├── StudentService.java
│   │   │       ├── SubjectService.java
│   │   │       └── impl/
│   │   │           ├── AttendanceServiceImpl.java
│   │   │           ├── AuthServiceImpl.java
│   │   │           ├── DashboardServiceImpl.java
│   │   │           ├── MarkServiceImpl.java
│   │   │           ├── ReportServiceImpl.java
│   │   │           ├── StudentServiceImpl.java
│   │   │           └── SubjectServiceImpl.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/                           # Embedded Frontend (Served at /)
│   │           ├── css/styles.css
│   │           ├── js/api.js
│   │           ├── js/app.js
│   │           └── index.html
│   └── test/
│       └── java/com/college/dashboard/
│           └── StudentDashboardApplicationTests.java
├── pom.xml
└── README.md
```

---

## 🚀 How to Run in VS Code on Windows

### Step 1: Open the Project in VS Code
1. Open Visual Studio Code.
2. Click **File** > **Open Folder...**
3. Select this folder: `c:\Users\DELL\OneDrive\Desktop\Student Attendance & Performance Dashboard`.

### Step 2: Configure MySQL
In `src/main/resources/application.properties`, the database settings are configured:
```properties
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:eventdb}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=eventapp
spring.datasource.password=event1234
```
> **Tip for Custom Credentials:** If you want to use your MySQL `root` account on another database (like `student_dashboard`), simply run `CREATE DATABASE student_dashboard;` in MySQL Workbench and change the username and password in `application.properties` to `root`.

### Step 3: Run the Application
Open a terminal in VS Code (`Ctrl + ~` or **Terminal > New Terminal**) and execute:

```cmd
mvn spring-boot:run
```

The application will start, connect to MySQL, auto-create tables, seed demo data, and start Tomcat on port **8080**.

### Step 4: Open in Your Browser
Open your browser and navigate to:
```text
http://localhost:8080
```

---

## 🔑 Demo Login Credentials

| Role | Username | Password | Access Level |
|---|---|---|---|
| **Administrator** | `admin` | `admin123` | Full access (Add, Edit, Delete students & marks) |
| **Teacher** | `teacher` | `teacher123` | Mark attendance, view analytics, enter marks, print reports |

*Quick Fill Buttons are available directly on the login screen for instant 1-click login.*

---

## 🧪 Testing Every Feature

1. **Login Test**: Click **"Demo Admin"** or **"Demo Teacher"** and hit **Sign In**.
2. **Dashboard Overview**: Check summary cards, the live Present vs Absent doughnut chart, and the Subject Performance bar chart.
3. **Student Management**:
   - Click **"Students"** in sidebar.
   - Use the live search bar to search for `"Aarav"` or filter by Department.
   - Click **"Add Student"** to register a new student (try duplicate roll `CSE2024001` to test duplicate validation!).
   - Click the pencil icon to edit student details.
4. **Attendance Management**:
   - Click **"Attendance"** in sidebar.
   - Select a subject and pick today's date.
   - Use **"All Present"** or customize individual Present/Absent toggles.
   - Click **"Save Attendance"**.
   - Check the **Attendance History** table below with student and date filters.
5. **Academic Marks**:
   - Click **"Marks & Grades"** in sidebar.
   - Click **"Enter Marks"**.
   - Enter `85` out of `100` and watch the live Grade preview change to `A`.
   - Click **"Save Marks"**.
6. **Report Cards & Print**:
   - Click **"Reports & Print"** in sidebar.
   - Select student **"CSE2024003 - Rohan Gupta"** (notice the attendance warning badge <75%).
   - Select student **"CSE2024001 - Aarav Patel"** to view a First Class with Distinction report.
   - Click **"Print Report Card"** to see clean printer formatting.
