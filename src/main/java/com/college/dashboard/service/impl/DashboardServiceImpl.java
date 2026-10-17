package com.college.dashboard.service.impl;

import com.college.dashboard.dto.DashboardStatsDto;
import com.college.dashboard.dto.StudentDto;
import com.college.dashboard.entity.AttendanceStatus;
import com.college.dashboard.repository.AttendanceRepository;
import com.college.dashboard.repository.MarkRepository;
import com.college.dashboard.repository.StudentRepository;
import com.college.dashboard.service.AttendanceService;
import com.college.dashboard.service.DashboardService;
import com.college.dashboard.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarkRepository markRepository;
    private final StudentService studentService;
    private final AttendanceService attendanceService;

    public DashboardServiceImpl(StudentRepository studentRepository,
                                AttendanceRepository attendanceRepository,
                                MarkRepository markRepository,
                                StudentService studentService,
                                AttendanceService attendanceService) {
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.markRepository = markRepository;
        this.studentService = studentService;
        this.attendanceService = attendanceService;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        // 1. Total Students
        long totalStudents = studentRepository.count();
        stats.setTotalStudents(totalStudents);

        // 2. Attendance Overview
        long totalPresent = attendanceRepository.countByStatus(AttendanceStatus.PRESENT);
        long totalAbsent = attendanceRepository.countByStatus(AttendanceStatus.ABSENT);
        stats.setTotalPresent(totalPresent);
        stats.setTotalAbsent(totalAbsent);

        long totalAttendanceRecords = totalPresent + totalAbsent;
        double avgAttendance = (totalAttendanceRecords > 0)
                ? ((double) totalPresent / totalAttendanceRecords) * 100.0
                : 0.0;
        stats.setAverageAttendance(Math.round(avgAttendance * 10.0) / 10.0);

        // 3. Academic Marks Overview
        Double avgMarks = markRepository.calculateOverallAveragePercentage();
        stats.setAverageMarks(avgMarks != null ? Math.round(avgMarks * 10.0) / 10.0 : 0.0);

        // 4. Subject-wise performance for Chart.js
        List<Object[]> subjectStats = markRepository.calculateSubjectAveragePercentages();
        List<String> subjectLabels = new ArrayList<>();
        List<Double> subjectAverages = new ArrayList<>();

        for (Object[] row : subjectStats) {
            String subjectName = (String) row[0];
            Number avg = (Number) row[1];
            subjectLabels.add(subjectName);
            subjectAverages.add(avg != null ? Math.round(avg.doubleValue() * 10.0) / 10.0 : 0.0);
        }
        stats.setSubjectLabels(subjectLabels);
        stats.setSubjectAverages(subjectAverages);

        // 5. Students at Risk (Attendance < 75% or Failing Marks)
        List<StudentDto> allStudents = studentService.getAllStudents();
        List<StudentDto> atRiskList = new ArrayList<>();
        Map<String, Long> departmentMap = new HashMap<>();

        for (StudentDto s : allStudents) {
            if (s.isAtRisk()) {
                atRiskList.add(s);
            }
            departmentMap.put(s.getDepartment(), departmentMap.getOrDefault(s.getDepartment(), 0L) + 1L);
        }

        stats.setStudentsAtRisk(atRiskList);
        stats.setStudentsAtRiskCount(atRiskList.size());
        stats.setStudentsByDepartment(departmentMap);

        // 6. Recent Attendance Feed
        stats.setRecentAttendance(attendanceService.getRecentAttendance());

        return stats;
    }
}
