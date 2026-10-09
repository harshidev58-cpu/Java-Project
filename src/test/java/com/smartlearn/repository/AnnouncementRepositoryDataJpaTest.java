package com.smartlearn.repository;

import com.smartlearn.model.Announcement;
import com.smartlearn.model.AnnouncementPriority;
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
 * Verifies announcement priority and recency ordering using persisted H2 records.
 * This data-layer test demonstrates that the JPQL ordering works through Hibernate and SQL.
 */
@DataJpaTest
class AnnouncementRepositoryDataJpaTest {
    @Autowired private TestEntityManager entityManager;
    @Autowired private AnnouncementRepository announcementRepository;

    @Test
    void findRelevantByStudentIdReturnsPriorityThenNewestFirst() {
        Course course = entityManager.persist(new Course(null, "CS101", "Computer Science", "Course"));
        Section section = entityManager.persist(new Section(null, "A", course, "Fall 2026"));
        Student student = entityManager.persist(new Student(null, "Jamie Learner", "jamie@example.edu"));
        entityManager.persist(new Enrollment(null, student, section, LocalDate.now()));

        LocalDateTime now = LocalDateTime.now();
        Announcement lowNewest = announcement("Low newest", AnnouncementPriority.LOW, now.minusMinutes(1), course, section);
        Announcement mediumNewest = announcement("Medium newest", AnnouncementPriority.MEDIUM, now.minusDays(1), course, section);
        Announcement highOld = announcement("High old", AnnouncementPriority.HIGH, now.minusDays(3), course, section);
        Announcement highNewest = announcement("High newest", AnnouncementPriority.HIGH, now.minusHours(1), course, section);
        entityManager.persist(lowNewest);
        entityManager.persist(mediumNewest);
        entityManager.persist(highOld);
        entityManager.persist(highNewest);
        entityManager.flush();
        entityManager.clear();

        List<String> titles = announcementRepository.findRelevantByStudentId(student.getId(),
                        AnnouncementPriority.HIGH, AnnouncementPriority.MEDIUM, AnnouncementPriority.LOW)
                .stream()
                .map(Announcement::getTitle)
                .toList();

        assertEquals(List.of("High newest", "High old", "Medium newest", "Low newest"), titles);
    }

    private Announcement announcement(String title, AnnouncementPriority priority, LocalDateTime publishedAt,
                                      Course course, Section section) {
        return new Announcement(null, title, "Message", course, section, section.getSemester(), priority, publishedAt);
    }
}
