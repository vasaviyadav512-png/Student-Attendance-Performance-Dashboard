package com.college.dashboard.service;

import com.college.dashboard.dto.SubjectDto;
import java.util.List;

public interface SubjectService {

    List<SubjectDto> getAllSubjects();

    SubjectDto getSubjectById(Long id);

    SubjectDto createSubject(SubjectDto dto);

    SubjectDto updateSubject(Long id, SubjectDto dto);

    void deleteSubject(Long id);

    List<SubjectDto> getSubjectsByDepartment(String department);
}
