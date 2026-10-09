package com.smartlearn.service;

import com.smartlearn.exception.EnrollmentNotFoundException;
import com.smartlearn.model.Assignment;
import com.smartlearn.model.AssignmentStatus;
import com.smartlearn.model.Enrollment;
import com.smartlearn.model.Student;
import com.smartlearn.repository.AssignmentRepository;
import com.smartlearn.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Finds assignments relevant to a student's section enrollments.
 * This service delegates filtering and ordering to Spring Data queries while keeping the feature-facing methods stable.
 */
@Service
public class AssignmentTrackerService {
    private final EnrollmentRepository enrollmentRepository;
    private final AssignmentRepository assignmentRepository;

    public AssignmentTrackerService(EnrollmentRepository enrollmentRepository,
                                    AssignmentRepository assignmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.assignmentRepository = assignmentRepository;
    }

    /** Returns pending assignments due now or later across all of the student's enrolled sections. */
    public List<Assignment> getUpcomingAssignments(Student student) {
        requireValidEnrollment(student);
        return assignmentRepository.findUpcomingByStudentId(
                student.getId(), AssignmentStatus.PENDING, LocalDateTime.now());
    }

    /** Returns overdue assignments across all of the student's enrolled sections, earliest deadline first. */
    public List<Assignment> getOverdueAssignments(Student student) {
        requireValidEnrollment(student);
        return assignmentRepository.findOverdueByStudentId(
                student.getId(), AssignmentStatus.OVERDUE, AssignmentStatus.PENDING, LocalDateTime.now());
    }

    /** Returns every assignment in the student's enrolled sections, ordered by deadline. */
    public List<Assignment> getAllAssignments(Student student) {
        requireValidEnrollment(student);
        return assignmentRepository.findAllForStudentOrderedByDueDate(student.getId());
    }

    private void requireValidEnrollment(Student student) {
        if (student == null || student.getId() == null) {
            throw new EnrollmentNotFoundException("A saved student is required to find assignments.");
        }

        boolean hasValidEnrollment = enrollmentRepository.findByStudentId(student.getId()).stream()
                .map(Enrollment::getSection)
                .anyMatch(section -> section != null && section.getId() != null && section.getCourse() != null);
        if (!hasValidEnrollment) {
            throw new EnrollmentNotFoundException("Student has no valid course enrollments: " + student.getId());
        }
    }
}
