package com.college.dashboard.controller;

import com.college.dashboard.dto.ApiResponse;
import com.college.dashboard.dto.AttendanceResponseDto;
import com.college.dashboard.dto.BatchAttendanceRequest;
import com.college.dashboard.entity.AttendanceStatus;
import com.college.dashboard.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/mark-batch")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDto>>> markBatchAttendance(
            @Valid @RequestBody BatchAttendanceRequest request) {
        List<AttendanceResponseDto> result = attendanceService.markBatchAttendance(request);
        return ResponseEntity.ok(ApiResponse.ok("Batch attendance saved successfully", result));
    }

    @PostMapping("/mark-single")
    public ResponseEntity<ApiResponse<AttendanceResponseDto>> markSingleAttendance(
            @RequestParam Long studentId,
            @RequestParam Long subjectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam AttendanceStatus status,
            @RequestParam(required = false) String remarks) {
        AttendanceResponseDto result = attendanceService.markSingleAttendance(studentId, subjectId, date, status, remarks);
        return ResponseEntity.ok(ApiResponse.ok("Attendance recorded successfully", result));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AttendanceResponseDto>>> getAllAttendance(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AttendanceResponseDto> list = attendanceService.filterAttendance(studentId, subjectId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/by-student/{studentId}")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDto>>> getAttendanceByStudent(
            @PathVariable Long studentId) {
        List<AttendanceResponseDto> list = attendanceService.getAttendanceByStudent(studentId);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDto>>> filterAttendance(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AttendanceResponseDto> list = attendanceService.filterAttendance(studentId, subjectId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDto>>> getRecentAttendance() {
        List<AttendanceResponseDto> list = attendanceService.getRecentAttendance();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }
}
