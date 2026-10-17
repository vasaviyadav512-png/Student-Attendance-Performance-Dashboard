package com.college.dashboard.service.impl;

import com.college.dashboard.dto.MarkRequestDto;
import com.college.dashboard.dto.MarkResponseDto;
import com.college.dashboard.entity.ExamType;
import com.college.dashboard.entity.Mark;
import com.college.dashboard.entity.Student;
import com.college.dashboard.entity.Subject;
import com.college.dashboard.exception.BadRequestException;
import com.college.dashboard.exception.ResourceNotFoundException;
import com.college.dashboard.repository.MarkRepository;
import com.college.dashboard.repository.StudentRepository;
import com.college.dashboard.repository.SubjectRepository;
import com.college.dashboard.service.MarkService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MarkServiceImpl implements MarkService {

    private final MarkRepository markRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    public MarkServiceImpl(MarkRepository markRepository,
                           StudentRepository studentRepository,
                           SubjectRepository subjectRepository) {
        this.markRepository = markRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    @Transactional
    public MarkResponseDto addOrUpdateMark(MarkRequestDto request) {
        if (request.getMarksObtained() > request.getMaxMarks()) {
            throw new BadRequestException("Marks obtained (" + request.getMarksObtained() + ") cannot be greater than maximum marks (" + request.getMaxMarks() + ").");
        }

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + request.getStudentId()));
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + request.getSubjectId()));

        Optional<Mark> existing = markRepository.findByStudentIdAndSubjectIdAndExamType(
                student.getId(),
                subject.getId(),
                request.getExamType()
        );

        String grade = Mark.calculateGrade(request.getMarksObtained(), request.getMaxMarks());

        Mark mark;
        if (existing.isPresent()) {
            mark = existing.get();
            mark.setMarksObtained(request.getMarksObtained());
            mark.setMaxMarks(request.getMaxMarks());
            mark.setGrade(grade);
            mark.setRemarks(request.getRemarks());
        } else {
            mark = new Mark(
                    student,
                    subject,
                    request.getExamType(),
                    request.getMarksObtained(),
                    request.getMaxMarks(),
                    grade,
                    request.getRemarks()
            );
        }

        Mark saved = markRepository.save(mark);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MarkResponseDto getMarkById(Long id) {
        Mark mark = markRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mark record not found with ID: " + id));
        return mapToResponseDto(mark);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarkResponseDto> getMarksByStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with ID: " + studentId);
        }
        List<Mark> marks = markRepository.findByStudentIdWithDetails(studentId);
        List<MarkResponseDto> result = new ArrayList<>();
        for (Mark m : marks) {
            result.add(mapToResponseDto(m));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarkResponseDto> getMarksBySubject(Long subjectId) {
        if (!subjectRepository.existsById(subjectId)) {
            throw new ResourceNotFoundException("Subject not found with ID: " + subjectId);
        }
        List<Mark> marks = markRepository.findBySubjectIdWithDetails(subjectId);
        List<MarkResponseDto> result = new ArrayList<>();
        for (Mark m : marks) {
            result.add(mapToResponseDto(m));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarkResponseDto> filterMarks(Long studentId, Long subjectId, ExamType examType) {
        List<Mark> marks = markRepository.filterMarks(studentId, subjectId, examType);
        List<MarkResponseDto> result = new ArrayList<>();
        for (Mark m : marks) {
            result.add(mapToResponseDto(m));
        }
        return result;
    }

    @Override
    @Transactional
    public void deleteMark(Long id) {
        if (!markRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mark record not found with ID: " + id);
        }
        markRepository.deleteById(id);
    }

    private MarkResponseDto mapToResponseDto(Mark mark) {
        double percentage = 0.0;
        if (mark.getMaxMarks() != null && mark.getMaxMarks() > 0) {
            percentage = (mark.getMarksObtained() / mark.getMaxMarks()) * 100.0;
            percentage = Math.round(percentage * 10.0) / 10.0;
        }

        return new MarkResponseDto(
                mark.getId(),
                mark.getStudent().getId(),
                mark.getStudent().getStudentId(),
                mark.getStudent().getName(),
                mark.getSubject().getId(),
                mark.getSubject().getSubjectCode(),
                mark.getSubject().getSubjectName(),
                mark.getExamType(),
                mark.getMarksObtained(),
                mark.getMaxMarks(),
                percentage,
                mark.getGrade(),
                mark.getRemarks(),
                mark.getCreatedAt()
        );
    }
}
