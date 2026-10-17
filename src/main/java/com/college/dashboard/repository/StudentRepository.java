package com.college.dashboard.repository;

import com.college.dashboard.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByStudentId(String studentId);

    boolean existsByStudentId(String studentId);

    boolean existsByEmail(String email);

    boolean existsByStudentIdAndIdNot(String studentId, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    List<Student> findByDepartment(String department);

    @Query("SELECT s FROM Student s WHERE " +
           "(:keyword IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(s.studentId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:department IS NULL OR s.department = :department) " +
           "ORDER BY s.studentId ASC")
    List<Student> searchStudents(@Param("keyword") String keyword, @Param("department") String department);

    @Query("SELECT DISTINCT s.department FROM Student s ORDER BY s.department ASC")
    List<String> findDistinctDepartments();
}
