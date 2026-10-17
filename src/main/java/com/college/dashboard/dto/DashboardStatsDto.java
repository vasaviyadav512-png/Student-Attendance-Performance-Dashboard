package com.college.dashboard.dto;

import java.util.List;
import java.util.Map;

public class DashboardStatsDto {

    private long totalStudents;
    private double averageAttendance;
    private double averageMarks;
    private long studentsAtRiskCount;

    // Charts Data
    private long totalPresent;
    private long totalAbsent;
    private List<String> subjectLabels;
    private List<Double> subjectAverages;
    private Map<String, Long> studentsByDepartment;

    // Recent activity & lists
    private List<AttendanceResponseDto> recentAttendance;
    private List<StudentDto> studentsAtRisk;

    public DashboardStatsDto() {
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public double getAverageAttendance() {
        return averageAttendance;
    }

    public void setAverageAttendance(double averageAttendance) {
        this.averageAttendance = averageAttendance;
    }

    public double getAverageMarks() {
        return averageMarks;
    }

    public void setAverageMarks(double averageMarks) {
        this.averageMarks = averageMarks;
    }

    public long getStudentsAtRiskCount() {
        return studentsAtRiskCount;
    }

    public void setStudentsAtRiskCount(long studentsAtRiskCount) {
        this.studentsAtRiskCount = studentsAtRiskCount;
    }

    public long getTotalPresent() {
        return totalPresent;
    }

    public void setTotalPresent(long totalPresent) {
        this.totalPresent = totalPresent;
    }

    public long getTotalAbsent() {
        return totalAbsent;
    }

    public void setTotalAbsent(long totalAbsent) {
        this.totalAbsent = totalAbsent;
    }

    public List<String> getSubjectLabels() {
        return subjectLabels;
    }

    public void setSubjectLabels(List<String> subjectLabels) {
        this.subjectLabels = subjectLabels;
    }

    public List<Double> getSubjectAverages() {
        return subjectAverages;
    }

    public void setSubjectAverages(List<Double> subjectAverages) {
        this.subjectAverages = subjectAverages;
    }

    public Map<String, Long> getStudentsByDepartment() {
        return studentsByDepartment;
    }

    public void setStudentsByDepartment(Map<String, Long> studentsByDepartment) {
        this.studentsByDepartment = studentsByDepartment;
    }

    public List<AttendanceResponseDto> getRecentAttendance() {
        return recentAttendance;
    }

    public void setRecentAttendance(List<AttendanceResponseDto> recentAttendance) {
        this.recentAttendance = recentAttendance;
    }

    public List<StudentDto> getStudentsAtRisk() {
        return studentsAtRisk;
    }

    public void setStudentsAtRisk(List<StudentDto> studentsAtRisk) {
        this.studentsAtRisk = studentsAtRisk;
    }
}
