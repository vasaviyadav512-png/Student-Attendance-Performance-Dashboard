package com.college.dashboard.service;

import com.college.dashboard.dto.MarkRequestDto;
import com.college.dashboard.dto.MarkResponseDto;
import com.college.dashboard.entity.ExamType;

import java.util.List;

public interface MarkService {

    MarkResponseDto addOrUpdateMark(MarkRequestDto request);

    MarkResponseDto getMarkById(Long id);

    List<MarkResponseDto> getMarksByStudent(Long studentId);

    List<MarkResponseDto> getMarksBySubject(Long subjectId);

    List<MarkResponseDto> filterMarks(Long studentId, Long subjectId, ExamType examType);

    void deleteMark(Long id);
}
