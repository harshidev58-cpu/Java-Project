package com.smartlearn.repository;

import com.smartlearn.model.Assignment;
import com.smartlearn.model.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Defines data access for trackable assignments.
 * This interface specializes the shared generic repository for the assignment model.
 */
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    @Query("""
            select a from Assignment a
            where exists (
                select e.id from Enrollment e
                where e.student.id = :studentId and e.section.id = a.section.id
            )
              and a.status = :pending and a.dueDate >= :now
            order by a.dueDate asc
            """)
    List<Assignment> findUpcomingByStudentId(@Param("studentId") Long studentId,
                                             @Param("pending") AssignmentStatus pending,
                                             @Param("now") LocalDateTime now);

    @Query("""
            select a from Assignment a
            where exists (
                select e.id from Enrollment e
                where e.student.id = :studentId and e.section.id = a.section.id
            )
              and (a.status = :overdue or (a.status = :pending and a.dueDate < :now))
            order by a.dueDate asc
            """)
    List<Assignment> findOverdueByStudentId(@Param("studentId") Long studentId,
                                            @Param("overdue") AssignmentStatus overdue,
                                            @Param("pending") AssignmentStatus pending,
                                            @Param("now") LocalDateTime now);

    @Query("""
            select a from Assignment a
            where exists (
                select e.id from Enrollment e
                where e.student.id = :studentId and e.section.id = a.section.id
            )
            order by a.dueDate asc
            """)
    List<Assignment> findAllForStudentOrderedByDueDate(@Param("studentId") Long studentId);
}
