package com.college.dashboard.repository;

import com.college.dashboard.entity.Attendance;
import com.college.dashboard.entity.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByStudentIdAndSubjectIdAndAttendanceDate(Long studentId, Long subjectId, LocalDate attendanceDate);

    List<Attendance> findByAttendanceDateAndSubjectId(LocalDate attendanceDate, Long subjectId);

    List<Attendance> findByStudentIdOrderByAttendanceDateDesc(Long studentId);

    List<Attendance> findBySubjectIdOrderByAttendanceDateDesc(Long subjectId);

    long countByStatus(AttendanceStatus status);

    long countByStudentId(Long studentId);

    long countByStudentIdAndStatus(Long studentId, AttendanceStatus status);

    @Query("SELECT a FROM Attendance a JOIN FETCH a.student JOIN FETCH a.subject " +
           "ORDER BY a.attendanceDate DESC, a.createdAt DESC")
    List<Attendance> findRecentAttendanceWithDetails();

    @Query("SELECT a FROM Attendance a JOIN FETCH a.student s JOIN FETCH a.subject sub WHERE " +
           "(:studentId IS NULL OR s.id = :studentId) AND " +
           "(:subjectId IS NULL OR sub.id = :subjectId) AND " +
           "(:startDate IS NULL OR a.attendanceDate >= :startDate) AND " +
           "(:endDate IS NULL OR a.attendanceDate <= :endDate) " +
           "ORDER BY a.attendanceDate DESC, s.name ASC")
    List<Attendance> filterAttendance(
            @Param("studentId") Long studentId,
            @Param("subjectId") Long subjectId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    @org.springframework.data.jpa.repository.Modifying
    @Query("DELETE FROM Attendance a WHERE a.student.id = :studentId")
    void deleteByStudentId(@Param("studentId") Long studentId);

    @org.springframework.data.jpa.repository.Modifying
    @Query("DELETE FROM Attendance a WHERE a.subject.id = :subjectId")
    void deleteBySubjectId(@Param("subjectId") Long subjectId);
}
