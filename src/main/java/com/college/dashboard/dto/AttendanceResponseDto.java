package com.college.dashboard.dto;

import com.college.dashboard.entity.AttendanceStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceResponseDto {

    private Long id;
    private Long studentId;
    private String studentRoll;
    private String studentName;
    private Long subjectId;
    private String subjectCode;
    private String subjectName;
    private LocalDate attendanceDate;
    private AttendanceStatus status;
    private String remarks;
    private LocalDateTime createdAt;

    public AttendanceResponseDto() {
    }

    public AttendanceResponseDto(Long id, Long studentId, String studentRoll, String studentName,
                                 Long subjectId, String subjectCode, String subjectName,
                                 LocalDate attendanceDate, AttendanceStatus status, String remarks,
                                 LocalDateTime createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.studentRoll = studentRoll;
        this.studentName = studentName;
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.remarks = remarks;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentRoll() {
        return studentRoll;
    }

    public void setStudentRoll(String studentRoll) {
        this.studentRoll = studentRoll;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
