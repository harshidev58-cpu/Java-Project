package com.smartlearn.service;

import com.smartlearn.model.Assignment;
import com.smartlearn.model.AssignmentStatus;
import com.smartlearn.model.Course;
import com.smartlearn.model.Enrollment;
import com.smartlearn.model.Section;
import com.smartlearn.model.Student;
import com.smartlearn.repository.AssignmentRepository;
import com.smartlearn.repository.EnrollmentRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Checks that upcoming assignments are returned in the order supplied by the sorted repository query.
 * This JUnit test demonstrates mocking collaborators while preserving the service's database-ordering contract.
 */
class AssignmentTrackerServiceTest {
    @Test
    void getUpcomingAssignmentsReturnsSoonestDeadlineFirst() {
        EnrollmentRepository enrollmentRepository = mock(EnrollmentRepository.class);
        AssignmentRepository assignmentRepository = mock(AssignmentRepository.class);
        Student student = new Student(4L, "Learner", "learner@example.edu");
        Section section = new Section(2L, "Section A", new Course(1L, "CS1", "Computing", ""), "Fall 2026");
        when(enrollmentRepository.findByStudentId(4L))
                .thenReturn(List.of(new Enrollment(1L, student, section, LocalDate.now())));

        Assignment soon = assignment("Soon", LocalDateTime.now().plusDays(1), section);
        Assignment later = assignment("Later", LocalDateTime.now().plusDays(5), section);
        when(assignmentRepository.findUpcomingByStudentId(eq(4L), eq(AssignmentStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(List.of(soon, later));

        AssignmentTrackerService service = new AssignmentTrackerService(enrollmentRepository, assignmentRepository);

        assertEquals(List.of(soon, later), service.getUpcomingAssignments(student));
        verify(assignmentRepository).findUpcomingByStudentId(eq(4L), eq(AssignmentStatus.PENDING), any(LocalDateTime.class));
    }

    private Assignment assignment(String title, LocalDateTime dueDate, Section section) {
        return new Assignment(null, title, "Description", dueDate, AssignmentStatus.PENDING, section);
    }
}
