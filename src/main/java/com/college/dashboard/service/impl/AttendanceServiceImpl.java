package com.college.dashboard.service.impl;

import com.college.dashboard.dto.AttendanceRecordDto;
import com.college.dashboard.dto.AttendanceResponseDto;
import com.college.dashboard.dto.BatchAttendanceRequest;
import com.college.dashboard.entity.Attendance;
import com.college.dashboard.entity.AttendanceStatus;
import com.college.dashboard.entity.Student;
import com.college.dashboard.entity.Subject;
import com.college.dashboard.exception.ResourceNotFoundException;
import com.college.dashboard.repository.AttendanceRepository;
import com.college.dashboard.repository.StudentRepository;
import com.college.dashboard.repository.SubjectRepository;
import com.college.dashboard.service.AttendanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository,
                                 StudentRepository studentRepository,
                                 SubjectRepository subjectRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    @Transactional
    public List<AttendanceResponseDto> markBatchAttendance(BatchAttendanceRequest request) {
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + request.getSubjectId()));

        List<AttendanceResponseDto> responses = new ArrayList<>();

        for (AttendanceRecordDto record : request.getRecords()) {
            Student student = studentRepository.findById(record.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + record.getStudentId()));

            Optional<Attendance> existing = attendanceRepository.findByStudentIdAndSubjectIdAndAttendanceDate(
                    student.getId(),
                    subject.getId(),
                    request.getAttendanceDate()
            );

            Attendance attendance;
            if (existing.isPresent()) {
                attendance = existing.get();
                attendance.setStatus(record.getStatus());
                if (record.getRemarks() != null) {
                    attendance.setRemarks(record.getRemarks());
                }
            } else {
                attendance = new Attendance(
                        student,
                        subject,
                        request.getAttendanceDate(),
                        record.getStatus(),
                        record.getRemarks() != null ? record.getRemarks() : (record.getStatus() == AttendanceStatus.PRESENT ? "Present" : "Absent")
                );
            }

            Attendance saved = attendanceRepository.save(attendance);
            responses.add(mapToResponseDto(saved));
        }

        return responses;
    }

    @Override
    @Transactional
    public AttendanceResponseDto markSingleAttendance(Long studentId, Long subjectId, LocalDate date, AttendanceStatus status, String remarks) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + subjectId));

        Optional<Attendance> existing = attendanceRepository.findByStudentIdAndSubjectIdAndAttendanceDate(studentId, subjectId, date);
        Attendance attendance;
        if (existing.isPresent()) {
            attendance = existing.get();
            attendance.setStatus(status);
            attendance.setRemarks(remarks);
        } else {
            attendance = new Attendance(student, subject, date, status, remarks);
        }

        Attendance saved = attendanceRepository.save(attendance);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> getAttendanceByStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with ID: " + studentId);
        }
        List<Attendance> list = attendanceRepository.findByStudentIdOrderByAttendanceDateDesc(studentId);
        List<AttendanceResponseDto> result = new ArrayList<>();
        for (Attendance a : list) {
            result.add(mapToResponseDto(a));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> getAttendanceByDateAndSubject(LocalDate date, Long subjectId) {
        List<Attendance> list = attendanceRepository.findByAttendanceDateAndSubjectId(date, subjectId);
        List<AttendanceResponseDto> result = new ArrayList<>();
        for (Attendance a : list) {
            result.add(mapToResponseDto(a));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> filterAttendance(Long studentId, Long subjectId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> list = attendanceRepository.filterAttendance(studentId, subjectId, startDate, endDate);
        List<AttendanceResponseDto> result = new ArrayList<>();
        for (Attendance a : list) {
            result.add(mapToResponseDto(a));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> getRecentAttendance() {
        List<Attendance> list = attendanceRepository.findRecentAttendanceWithDetails();
        List<AttendanceResponseDto> result = new ArrayList<>();
        int count = 0;
        for (Attendance a : list) {
            if (count++ >= 10) break;
            result.add(mapToResponseDto(a));
        }
        return result;
    }

    private AttendanceResponseDto mapToResponseDto(Attendance a) {
        return new AttendanceResponseDto(
                a.getId(),
                a.getStudent().getId(),
                a.getStudent().getStudentId(),
                a.getStudent().getName(),
                a.getSubject().getId(),
                a.getSubject().getSubjectCode(),
                a.getSubject().getSubjectName(),
                a.getAttendanceDate(),
                a.getStatus(),
                a.getRemarks(),
                a.getCreatedAt()
        );
    }
}
