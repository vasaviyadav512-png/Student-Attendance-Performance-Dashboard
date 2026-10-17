package com.college.dashboard.service;

import com.college.dashboard.dto.StudentReportDto;
import java.util.List;

public interface ReportService {

    StudentReportDto generateStudentReport(Long studentId);

    List<StudentReportDto> generateBatchReports(String department);
}
