package com.college.dashboard.service.impl;

import com.college.dashboard.dto.MarkResponseDto;
import com.college.dashboard.dto.StudentDto;
import com.college.dashboard.dto.StudentReportDto;
import com.college.dashboard.entity.AttendanceStatus;
import com.college.dashboard.entity.Mark;
import com.college.dashboard.repository.AttendanceRepository;
import com.college.dashboard.service.MarkService;
import com.college.dashboard.service.ReportService;
import com.college.dashboard.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final StudentService studentService;
    private final AttendanceRepository attendanceRepository;
    private final MarkService markService;

    public ReportServiceImpl(StudentService studentService,
                             AttendanceRepository attendanceRepository,
                             MarkService markService) {
        this.studentService = studentService;
        this.attendanceRepository = attendanceRepository;
        this.markService = markService;
    }

    @Override
    @Transactional(readOnly = true)
    public StudentReportDto generateStudentReport(Long studentId) {
        StudentDto student = studentService.getStudentById(studentId);
        StudentReportDto report = new StudentReportDto();
        report.setStudent(student);

        // 1. Attendance Metrics
        long totalClasses = attendanceRepository.countByStudentId(studentId);
        long attendedClasses = attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.PRESENT);
        long missedClasses = totalClasses - attendedClasses;

        report.setTotalClasses(totalClasses);
        report.setClassesAttended(attendedClasses);
        report.setClassesMissed(missedClasses);

        double attRate = (totalClasses > 0) ? ((double) attendedClasses / totalClasses) * 100.0 : 0.0;
        report.setAttendancePercentage(Math.round(attRate * 10.0) / 10.0);
        report.setAttendanceStatus(attRate >= 75.0 ? "Satisfactory Attendance (Eligible for Exams)" : "Attendance Shortage (Below 75% Requirement)");

        // 2. Marks and Academic Standing
        List<MarkResponseDto> marks = markService.getMarksByStudent(studentId);
        report.setMarks(marks);

        double totalObtained = 0.0;
        double totalMax = 0.0;

        for (MarkResponseDto m : marks) {
            totalObtained += m.getMarksObtained();
            totalMax += m.getMaxMarks();
        }

        double overallPct = (totalMax > 0) ? (totalObtained / totalMax) * 100.0 : 0.0;
        report.setOverallPercentage(Math.round(overallPct * 10.0) / 10.0);
        report.setOverallGrade(Mark.calculateGrade(totalObtained, totalMax));

        if (totalMax == 0) {
            report.setAcademicStanding("No Exam Records Available");
        } else if (overallPct >= 85.0) {
            report.setAcademicStanding("First Class with Distinction (Excellent)");
        } else if (overallPct >= 70.0) {
            report.setAcademicStanding("First Class");
        } else if (overallPct >= 55.0) {
            report.setAcademicStanding("Second Class");
        } else if (overallPct >= 40.0) {
            report.setAcademicStanding("Pass");
        } else {
            report.setAcademicStanding("Academic Remedial Support Required (Fail)");
        }

        return report;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentReportDto> generateBatchReports(String department) {
        List<StudentDto> students = (department != null && !department.isBlank())
                ? studentService.searchStudents(null, department)
                : studentService.getAllStudents();

        List<StudentReportDto> reports = new ArrayList<>();
        for (StudentDto s : students) {
            reports.add(generateStudentReport(s.getId()));
        }
        return reports;
    }
}
