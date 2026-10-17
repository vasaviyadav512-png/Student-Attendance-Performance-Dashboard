package com.college.dashboard.service.impl;

import com.college.dashboard.dto.StudentDto;
import com.college.dashboard.entity.AttendanceStatus;
import com.college.dashboard.entity.Student;
import com.college.dashboard.exception.DuplicateResourceException;
import com.college.dashboard.exception.ResourceNotFoundException;
import com.college.dashboard.repository.AttendanceRepository;
import com.college.dashboard.repository.MarkRepository;
import com.college.dashboard.repository.StudentRepository;
import com.college.dashboard.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarkRepository markRepository;

    public StudentServiceImpl(StudentRepository studentRepository,
                              AttendanceRepository attendanceRepository,
                              MarkRepository markRepository) {
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.markRepository = markRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        List<StudentDto> dtoList = new ArrayList<>();
        for (Student s : students) {
            dtoList.add(mapToDtoWithMetrics(s));
        }
        return dtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDto getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
        return mapToDtoWithMetrics(student);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDto getStudentByRoll(String studentId) {
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with Roll/ID: " + studentId));
        return mapToDtoWithMetrics(student);
    }

    @Override
    @Transactional
    public StudentDto createStudent(StudentDto dto) {
        if (studentRepository.existsByStudentId(dto.getStudentId())) {
            throw new DuplicateResourceException("A student with ID '" + dto.getStudentId() + "' already exists.");
        }
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("A student with Email '" + dto.getEmail() + "' already exists.");
        }

        Student student = new Student(
                dto.getStudentId().trim().toUpperCase(),
                dto.getName().trim(),
                dto.getEmail().trim().toLowerCase(),
                dto.getDepartment().trim(),
                dto.getYear(),
                dto.getSection().trim().toUpperCase()
        );

        Student saved = studentRepository.save(student);
        return mapToDtoWithMetrics(saved);
    }

    @Override
    @Transactional
    public StudentDto updateStudent(Long id, StudentDto dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        if (studentRepository.existsByStudentIdAndIdNot(dto.getStudentId(), id)) {
            throw new DuplicateResourceException("Another student with ID '" + dto.getStudentId() + "' already exists.");
        }
        if (studentRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new DuplicateResourceException("Another student with Email '" + dto.getEmail() + "' already exists.");
        }

        student.setStudentId(dto.getStudentId().trim().toUpperCase());
        student.setName(dto.getName().trim());
        student.setEmail(dto.getEmail().trim().toLowerCase());
        student.setDepartment(dto.getDepartment().trim());
        student.setYear(dto.getYear());
        student.setSection(dto.getSection().trim().toUpperCase());

        Student updated = studentRepository.save(student);
        return mapToDtoWithMetrics(updated);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with ID: " + id);
        }
        attendanceRepository.deleteByStudentId(id);
        markRepository.deleteByStudentId(id);
        studentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> searchStudents(String keyword, String department) {
        String cleanKeyword = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        String cleanDepartment = (department != null && !department.isBlank()) ? department.trim() : null;

        List<Student> students = studentRepository.searchStudents(cleanKeyword, cleanDepartment);
        List<StudentDto> result = new ArrayList<>();
        for (Student s : students) {
            result.add(mapToDtoWithMetrics(s));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllDepartments() {
        return studentRepository.findDistinctDepartments();
    }

    public StudentDto mapToDtoWithMetrics(Student student) {
        StudentDto dto = new StudentDto(
                student.getId(),
                student.getStudentId(),
                student.getName(),
                student.getEmail(),
                student.getDepartment(),
                student.getYear(),
                student.getSection()
        );
        dto.setCreatedAt(student.getCreatedAt());

        // 1. Calculate Attendance Percentage
        long totalAtt = attendanceRepository.countByStudentId(student.getId());
        long presentAtt = attendanceRepository.countByStudentIdAndStatus(student.getId(), AttendanceStatus.PRESENT);
        double attRate = (totalAtt > 0) ? ((double) presentAtt / totalAtt) * 100.0 : 0.0;
        dto.setAttendancePercentage(Math.round(attRate * 10.0) / 10.0);

        // 2. Calculate Average Marks
        Double avgMarks = markRepository.calculateStudentAveragePercentage(student.getId());
        double marksRate = (avgMarks != null) ? avgMarks : 0.0;
        dto.setAverageMarksPercentage(Math.round(marksRate * 10.0) / 10.0);

        // 3. Determine Risk Status (Attendance < 75% or Marks < 40%)
        boolean isAttRisk = totalAtt > 0 && attRate < 75.0;
        boolean isMarksRisk = avgMarks != null && marksRate < 40.0;

        if (isAttRisk && isMarksRisk) {
            dto.setAtRisk(true);
            dto.setRiskReason("Low Attendance (" + dto.getAttendancePercentage() + "%) & Low Marks (" + dto.getAverageMarksPercentage() + "%)");
        } else if (isAttRisk) {
            dto.setAtRisk(true);
            dto.setRiskReason("Low Attendance (" + dto.getAttendancePercentage() + "% < 75%)");
        } else if (isMarksRisk) {
            dto.setAtRisk(true);
            dto.setRiskReason("Failing Academic Average (" + dto.getAverageMarksPercentage() + "% < 40%)");
        } else {
            dto.setAtRisk(false);
            dto.setRiskReason("Good Standing");
        }

        return dto;
    }
}
