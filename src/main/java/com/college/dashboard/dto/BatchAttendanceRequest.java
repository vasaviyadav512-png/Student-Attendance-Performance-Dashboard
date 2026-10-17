package com.college.dashboard.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class BatchAttendanceRequest {

    @NotNull(message = "Subject is required")
    private Long subjectId;

    @NotNull(message = "Attendance date is required")
    private LocalDate attendanceDate;

    @NotEmpty(message = "Attendance records cannot be empty")
    @Valid
    private List<AttendanceRecordDto> records;

    public BatchAttendanceRequest() {
    }

    public BatchAttendanceRequest(Long subjectId, LocalDate attendanceDate, List<AttendanceRecordDto> records) {
        this.subjectId = subjectId;
        this.attendanceDate = attendanceDate;
        this.records = records;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public List<AttendanceRecordDto> getRecords() {
        return records;
    }

    public void setRecords(List<AttendanceRecordDto> records) {
        this.records = records;
    }
}
