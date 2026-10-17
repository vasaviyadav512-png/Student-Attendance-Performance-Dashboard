package com.college.dashboard.service;

import com.college.dashboard.dto.AttendanceResponseDto;
import com.college.dashboard.dto.BatchAttendanceRequest;
import com.college.dashboard.entity.AttendanceStatus;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    List<AttendanceResponseDto> markBatchAttendance(BatchAttendanceRequest request);

    AttendanceResponseDto markSingleAttendance(Long studentId, Long subjectId, LocalDate date, AttendanceStatus status, String remarks);

    List<AttendanceResponseDto> getAttendanceByStudent(Long studentId);

    List<AttendanceResponseDto> getAttendanceByDateAndSubject(LocalDate date, Long subjectId);

    List<AttendanceResponseDto> filterAttendance(Long studentId, Long subjectId, LocalDate startDate, LocalDate endDate);

    List<AttendanceResponseDto> getRecentAttendance();
}
