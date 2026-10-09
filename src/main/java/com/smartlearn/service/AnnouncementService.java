package com.smartlearn.service;

import com.smartlearn.exception.EnrollmentNotFoundException;
import com.smartlearn.model.Announcement;
import com.smartlearn.model.AnnouncementPriority;
import com.smartlearn.model.Enrollment;
import com.smartlearn.model.Student;
import com.smartlearn.repository.AnnouncementRepository;
import com.smartlearn.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Selects announcements that apply to a student's actual enrollments.
 * This service delegates relationship filtering and priority/recency sorting to a Spring Data JPQL query.
 */
@Service
public class AnnouncementService {
    private final EnrollmentRepository enrollmentRepository;
    private final AnnouncementRepository announcementRepository;

    public AnnouncementService(EnrollmentRepository enrollmentRepository,
                               AnnouncementRepository announcementRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.announcementRepository = announcementRepository;
    }

    /** Returns matching announcements, with higher priorities and newer messages first. */
    public List<Announcement> getRelevantAnnouncements(Student student) {
        if (student == null || student.getId() == null) {
            throw new EnrollmentNotFoundException("A saved student is required to find announcements.");
        }

        boolean hasValidEnrollment = enrollmentRepository.findByStudentId(student.getId()).stream()
                .map(Enrollment::getSection)
                .anyMatch(section -> section != null && section.getId() != null && section.getCourse() != null);
        if (!hasValidEnrollment) {
            throw new EnrollmentNotFoundException("Student has no valid course enrollments: " + student.getId());
        }

        return announcementRepository.findRelevantByStudentId(student.getId(),
                AnnouncementPriority.HIGH, AnnouncementPriority.MEDIUM, AnnouncementPriority.LOW);
    }
}
