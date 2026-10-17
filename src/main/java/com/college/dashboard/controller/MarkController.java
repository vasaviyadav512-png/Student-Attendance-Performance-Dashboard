package com.college.dashboard.controller;

import com.college.dashboard.dto.ApiResponse;
import com.college.dashboard.dto.MarkRequestDto;
import com.college.dashboard.dto.MarkResponseDto;
import com.college.dashboard.entity.ExamType;
import com.college.dashboard.service.MarkService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marks")
public class MarkController {

    private final MarkService markService;

    public MarkController(MarkService markService) {
        this.markService = markService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MarkResponseDto>> addOrUpdateMark(
            @Valid @RequestBody MarkRequestDto request) {
        MarkResponseDto saved = markService.addOrUpdateMark(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Mark record saved successfully", saved));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MarkResponseDto>>> getAllMarks(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) ExamType examType) {
        List<MarkResponseDto> list = markService.filterMarks(studentId, subjectId, examType);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MarkResponseDto>> getMarkById(@PathVariable Long id) {
        MarkResponseDto mark = markService.getMarkById(id);
        return ResponseEntity.ok(ApiResponse.ok(mark));
    }

    @GetMapping("/by-student/{studentId}")
    public ResponseEntity<ApiResponse<List<MarkResponseDto>>> getMarksByStudent(@PathVariable Long studentId) {
        List<MarkResponseDto> list = markService.getMarksByStudent(studentId);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/by-subject/{subjectId}")
    public ResponseEntity<ApiResponse<List<MarkResponseDto>>> getMarksBySubject(@PathVariable Long subjectId) {
        List<MarkResponseDto> list = markService.getMarksBySubject(subjectId);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<MarkResponseDto>>> filterMarks(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) ExamType examType) {
        List<MarkResponseDto> list = markService.filterMarks(studentId, subjectId, examType);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMark(@PathVariable Long id) {
        markService.deleteMark(id);
        return ResponseEntity.ok(ApiResponse.ok("Mark record deleted successfully", null));
    }
}
