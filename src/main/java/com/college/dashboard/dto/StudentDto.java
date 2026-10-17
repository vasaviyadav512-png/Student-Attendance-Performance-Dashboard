package com.college.dashboard.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class StudentDto {

    private Long id;

    @NotBlank(message = "Student Roll / ID is required")
    private String studentId;

    @NotBlank(message = "Student Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Department is required")
    private String department;

    @NotNull(message = "Year is required")
    @Min(value = 1, message = "Year must be between 1 and 4")
    @Max(value = 4, message = "Year must be between 1 and 4")
    private Integer year;

    @NotBlank(message = "Section is required")
    private String section;

    private LocalDateTime createdAt;

    // Computed / Summary fields
    private Double attendancePercentage;
    private Double averageMarksPercentage;
    private boolean atRisk;
    private String riskReason;

    public StudentDto() {
    }

    public StudentDto(Long id, String studentId, String name, String email, String department, Integer year, String section) {
        this.id = id;
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.department = department;
        this.year = year;
        this.section = section;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(Double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    public Double getAverageMarksPercentage() {
        return averageMarksPercentage;
    }

    public void setAverageMarksPercentage(Double averageMarksPercentage) {
        this.averageMarksPercentage = averageMarksPercentage;
    }

    public boolean isAtRisk() {
        return atRisk;
    }

    public void setAtRisk(boolean atRisk) {
        this.atRisk = atRisk;
    }

    public String getRiskReason() {
        return riskReason;
    }

    public void setRiskReason(String riskReason) {
        this.riskReason = riskReason;
    }
}
