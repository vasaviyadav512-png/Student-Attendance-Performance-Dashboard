package com.college.dashboard.service;

import com.college.dashboard.dto.StudentDto;
import java.util.List;

public interface StudentService {

    List<StudentDto> getAllStudents();

    StudentDto getStudentById(Long id);

    StudentDto getStudentByRoll(String studentId);

    StudentDto createStudent(StudentDto dto);

    StudentDto updateStudent(Long id, StudentDto dto);

    void deleteStudent(Long id);

    List<StudentDto> searchStudents(String keyword, String department);

    List<String> getAllDepartments();
}
