package com.college.dashboard.repository;

import com.college.dashboard.entity.ExamType;
import com.college.dashboard.entity.Mark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarkRepository extends JpaRepository<Mark, Long> {

    Optional<Mark> findByStudentIdAndSubjectIdAndExamType(Long studentId, Long subjectId, ExamType examType);

    @Query("SELECT m FROM Mark m JOIN FETCH m.student JOIN FETCH m.subject WHERE m.student.id = :studentId ORDER BY m.subject.subjectCode ASC, m.examType ASC")
    List<Mark> findByStudentIdWithDetails(@Param("studentId") Long studentId);

    @Query("SELECT m FROM Mark m JOIN FETCH m.student JOIN FETCH m.subject WHERE m.subject.id = :subjectId ORDER BY m.student.name ASC")
    List<Mark> findBySubjectIdWithDetails(@Param("subjectId") Long subjectId);

    @Query("SELECT m FROM Mark m JOIN FETCH m.student s JOIN FETCH m.subject sub WHERE " +
           "(:studentId IS NULL OR s.id = :studentId) AND " +
           "(:subjectId IS NULL OR sub.id = :subjectId) AND " +
           "(:examType IS NULL OR m.examType = :examType) " +
           "ORDER BY s.studentId ASC, sub.subjectCode ASC")
    List<Mark> filterMarks(
            @Param("studentId") Long studentId,
            @Param("subjectId") Long subjectId,
            @Param("examType") ExamType examType
    );

    @Query("SELECT AVG((m.marksObtained / m.maxMarks) * 100.0) FROM Mark m")
    Double calculateOverallAveragePercentage();

    @Query("SELECT m.subject.subjectName, AVG((m.marksObtained / m.maxMarks) * 100.0) " +
           "FROM Mark m GROUP BY m.subject.id, m.subject.subjectName ORDER BY m.subject.subjectName ASC")
    List<Object[]> calculateSubjectAveragePercentages();

    @Query("SELECT AVG((m.marksObtained / m.maxMarks) * 100.0) FROM Mark m WHERE m.student.id = :studentId")
    Double calculateStudentAveragePercentage(@Param("studentId") Long studentId);

    @org.springframework.data.jpa.repository.Modifying
    @Query("DELETE FROM Mark m WHERE m.student.id = :studentId")
    void deleteByStudentId(@Param("studentId") Long studentId);

    @org.springframework.data.jpa.repository.Modifying
    @Query("DELETE FROM Mark m WHERE m.subject.id = :subjectId")
    void deleteBySubjectId(@Param("subjectId") Long subjectId);
}
