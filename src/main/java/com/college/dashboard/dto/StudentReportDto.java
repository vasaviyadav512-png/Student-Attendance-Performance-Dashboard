package com.college.dashboard.dto;

import java.time.LocalDate;
import java.util.List;

public class StudentReportDto {

    private StudentDto student;
    private LocalDate reportDate;

    // Attendance summary
    private long totalClasses;
    private long classesAttended;
    private long classesMissed;
    private double attendancePercentage;
    private String attendanceStatus;

    // Academic performance summary
    private List<MarkResponseDto> marks;
    private double overallPercentage;
    private String overallGrade;
    private String academicStanding;

    public StudentReportDto() {
        this.reportDate = LocalDate.now();
    }

    public StudentDto getStudent() {
        return student;
    }

    public void setStudent(StudentDto student) {
        this.student = student;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public long getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(long totalClasses) {
        this.totalClasses = totalClasses;
    }

    public long getClassesAttended() {
        return classesAttended;
    }

    public void setClassesAttended(long classesAttended) {
        this.classesAttended = classesAttended;
    }

    public long getClassesMissed() {
        return classesMissed;
    }

    public void setClassesMissed(long classesMissed) {
        this.classesMissed = classesMissed;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public List<MarkResponseDto> getMarks() {
        return marks;
    }

    public void setMarks(List<MarkResponseDto> marks) {
        this.marks = marks;
    }

    public double getOverallPercentage() {
        return overallPercentage;
    }

    public void setOverallPercentage(double overallPercentage) {
        this.overallPercentage = overallPercentage;
    }

    public String getOverallGrade() {
        return overallGrade;
    }

    public void setOverallGrade(String overallGrade) {
        this.overallGrade = overallGrade;
    }

    public String getAcademicStanding() {
        return academicStanding;
    }

    public void setAcademicStanding(String academicStanding) {
        this.academicStanding = academicStanding;
    }
}
