package com.smartlearn.service;

import com.smartlearn.model.Announcement;
import com.smartlearn.model.AnnouncementPriority;
import com.smartlearn.model.Course;
import com.smartlearn.model.Enrollment;
import com.smartlearn.model.Section;
import com.smartlearn.model.Student;
import com.smartlearn.repository.AnnouncementRepository;
import com.smartlearn.repository.EnrollmentRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Checks the priority ordering returned by the announcement query.
 * This JUnit test demonstrates focused service testing with mocked persistence collaborators.
 */
class AnnouncementServiceTest {
    @Test
    void getRelevantAnnouncementsReturnsHighPriorityFirst() {
        EnrollmentRepository enrollmentRepository = mock(EnrollmentRepository.class);
        AnnouncementRepository announcementRepository = mock(AnnouncementRepository.class);
        Student student = new Student(4L, "Learner", "learner@example.edu");
        Section section = new Section(2L, "Section A", new Course(1L, "CS1", "Computing", ""), "Fall 2026");
        when(enrollmentRepository.findByStudentId(4L))
                .thenReturn(List.of(new Enrollment(1L, student, section, LocalDate.now())));

        Announcement high = announcement("Exam moved", AnnouncementPriority.HIGH);
        Announcement low = announcement("Reading suggestion", AnnouncementPriority.LOW);
        when(announcementRepository.findRelevantByStudentId(4L,
                AnnouncementPriority.HIGH, AnnouncementPriority.MEDIUM, AnnouncementPriority.LOW))
                .thenReturn(List.of(high, low));

        AnnouncementService service = new AnnouncementService(enrollmentRepository, announcementRepository);

        assertEquals(List.of(high, low), service.getRelevantAnnouncements(student));
        verify(announcementRepository).findRelevantByStudentId(4L,
                AnnouncementPriority.HIGH, AnnouncementPriority.MEDIUM, AnnouncementPriority.LOW);
    }

    private Announcement announcement(String title, AnnouncementPriority priority) {
        return new Announcement(null, title, "Message", null, null, null, priority, LocalDateTime.now());
    }
}
