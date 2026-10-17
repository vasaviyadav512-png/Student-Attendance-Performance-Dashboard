package com.college.dashboard.service.impl;

import com.college.dashboard.dto.SubjectDto;
import com.college.dashboard.entity.Subject;
import com.college.dashboard.exception.DuplicateResourceException;
import com.college.dashboard.exception.ResourceNotFoundException;
import com.college.dashboard.repository.AttendanceRepository;
import com.college.dashboard.repository.MarkRepository;
import com.college.dashboard.repository.SubjectRepository;
import com.college.dashboard.service.SubjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarkRepository markRepository;

    public SubjectServiceImpl(SubjectRepository subjectRepository,
                              AttendanceRepository attendanceRepository,
                              MarkRepository markRepository) {
        this.subjectRepository = subjectRepository;
        this.attendanceRepository = attendanceRepository;
        this.markRepository = markRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectDto> getAllSubjects() {
        List<Subject> subjects = subjectRepository.findAllByOrderBySubjectCodeAsc();
        List<SubjectDto> result = new ArrayList<>();
        for (Subject s : subjects) {
            result.add(mapToDto(s));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectDto getSubjectById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + id));
        return mapToDto(subject);
    }

    @Override
    @Transactional
    public SubjectDto createSubject(SubjectDto dto) {
        if (subjectRepository.existsBySubjectCode(dto.getSubjectCode())) {
            throw new DuplicateResourceException("Subject code '" + dto.getSubjectCode() + "' already exists.");
        }

        Subject subject = new Subject(
                dto.getSubjectCode().trim().toUpperCase(),
                dto.getSubjectName().trim(),
                dto.getDepartment().trim(),
                dto.getSemester(),
                dto.getCredits()
        );

        Subject saved = subjectRepository.save(subject);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public SubjectDto updateSubject(Long id, SubjectDto dto) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + id));

        if (subjectRepository.existsBySubjectCodeAndIdNot(dto.getSubjectCode(), id)) {
            throw new DuplicateResourceException("Another subject with code '" + dto.getSubjectCode() + "' already exists.");
        }

        subject.setSubjectCode(dto.getSubjectCode().trim().toUpperCase());
        subject.setSubjectName(dto.getSubjectName().trim());
        subject.setDepartment(dto.getDepartment().trim());
        subject.setSemester(dto.getSemester());
        subject.setCredits(dto.getCredits());

        Subject updated = subjectRepository.save(subject);
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void deleteSubject(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subject not found with ID: " + id);
        }
        attendanceRepository.deleteBySubjectId(id);
        markRepository.deleteBySubjectId(id);
        subjectRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectDto> getSubjectsByDepartment(String department) {
        List<Subject> subjects = subjectRepository.findByDepartment(department);
        List<SubjectDto> result = new ArrayList<>();
        for (Subject s : subjects) {
            result.add(mapToDto(s));
        }
        return result;
    }

    private SubjectDto mapToDto(Subject subject) {
        SubjectDto dto = new SubjectDto(
                subject.getId(),
                subject.getSubjectCode(),
                subject.getSubjectName(),
                subject.getDepartment(),
                subject.getSemester(),
                subject.getCredits()
        );
        dto.setCreatedAt(subject.getCreatedAt());
        return dto;
    }
}
