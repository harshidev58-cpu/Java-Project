package com.smartlearn.repository;

import com.smartlearn.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Defines enrollment data access needed by student-specific features.
 * This interface specializes the generic repository with a student lookup operation.
 */
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    @Query("select e from Enrollment e where e.student.id = :studentId")
    List<Enrollment> findByStudentId(@Param("studentId") Long studentId);

    @Query("select e from Enrollment e where e.section.id = :sectionId")
    List<Enrollment> findBySectionId(@Param("sectionId") Long sectionId);
}
