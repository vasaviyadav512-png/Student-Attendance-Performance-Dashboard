package com.college.dashboard.controller;

import com.college.dashboard.dto.ApiResponse;
import com.college.dashboard.dto.SubjectDto;
import com.college.dashboard.service.SubjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubjectDto>>> getAllSubjects(
            @RequestParam(required = false) String department) {
        List<SubjectDto> list = (department != null && !department.isBlank())
                ? subjectService.getSubjectsByDepartment(department)
                : subjectService.getAllSubjects();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectDto>> getSubjectById(@PathVariable Long id) {
        SubjectDto subject = subjectService.getSubjectById(id);
        return ResponseEntity.ok(ApiResponse.ok(subject));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubjectDto>> createSubject(@Valid @RequestBody SubjectDto dto) {
        SubjectDto created = subjectService.createSubject(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Subject created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectDto>> updateSubject(
            @PathVariable Long id,
            @Valid @RequestBody SubjectDto dto) {
        SubjectDto updated = subjectService.updateSubject(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Subject updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.ok(ApiResponse.ok("Subject deleted successfully", null));
    }
}
