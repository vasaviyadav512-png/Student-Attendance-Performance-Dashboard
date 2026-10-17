package com.college.dashboard.controller;

import com.college.dashboard.dto.ApiResponse;
import com.college.dashboard.dto.StudentReportDto;
import com.college.dashboard.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<StudentReportDto>> getStudentReport(@PathVariable Long studentId) {
        StudentReportDto report = reportService.generateStudentReport(studentId);
        return ResponseEntity.ok(ApiResponse.ok(report));
    }

    @GetMapping("/batch")
    public ResponseEntity<ApiResponse<List<StudentReportDto>>> getBatchReports(
            @RequestParam(required = false) String department) {
        List<StudentReportDto> reports = reportService.generateBatchReports(department);
        return ResponseEntity.ok(ApiResponse.ok(reports));
    }
}
