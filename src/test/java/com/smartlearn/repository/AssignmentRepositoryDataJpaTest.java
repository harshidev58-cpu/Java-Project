package com.smartlearn.repository;

import com.smartlearn.model.Assignment;
import com.smartlearn.model.AssignmentStatus;
import com.smartlearn.model.Course;
import com.smartlearn.model.Enrollment;
import com.smartlearn.model.Section;
import com.smartlearn.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies the assignment ordering query against persisted H2 rows.
 * This data-layer test demonstrates repository integration testing with a real JPA provider.
 */
@DataJpaTest
class AssignmentRepositoryDataJpaTest {
    @Autowired private TestEntityManager entityManager;
    @Autowired private AssignmentRepository assignmentRepository;

    @Test
    void findUpcomingByStudentIdReturnsAssignmentsByAscendingDueDate() {
        Course course = entityManager.persist(new Course(null, "CS101", "Computer Science", "Course"));
        Section section = entityManager.persist(new Section(null, "A", course, "Fall 2026"));
        Student student = entityManager.persist(new Student(null, "Jamie Learner", "jamie@example.edu"));
        entityManager.persist(new Enrollment(null, student, section, LocalDate.now()));

        LocalDateTime now = LocalDateTime.now();
        Assignment middle = new Assignment(null, "Middle", "", now.plusDays(3), AssignmentStatus.PENDING, section);
        Assignment latest = new Assignment(null, "Latest", "", now.plusDays(8), AssignmentStatus.PENDING, section);
        Assignment earliest = new Assignment(null, "Earliest", "", now.plusDays(1), AssignmentStatus.PENDING, section);
        entityManager.persist(middle);
        entityManager.persist(latest);
        entityManager.persist(earliest);
        entityManager.flush();
        entityManager.clear();

        List<String> titles = assignmentRepository.findUpcomingByStudentId(
                        student.getId(), AssignmentStatus.PENDING, now)
                .stream()
                .map(Assignment::getTitle)
                .toList();

        assertEquals(List.of("Earliest", "Middle", "Latest"), titles);
    }
}
