package com.smartlearn.service;

import com.smartlearn.model.Assignment;
import com.smartlearn.model.AssignmentStatus;
import com.smartlearn.model.Enrollment;
import com.smartlearn.model.Student;
import com.smartlearn.repository.AssignmentRepository;
import com.smartlearn.repository.EnrollmentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Checks for assignments approaching their deadline and prepares student reminders.
 * This service demonstrates Spring scheduling; the notification sender is intentionally left as a stub.
 */
@Service
public class AssignmentReminderService {
    private final AssignmentRepository assignmentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public AssignmentReminderService(AssignmentRepository assignmentRepository,
                                    EnrollmentRepository enrollmentRepository) {
        this.assignmentRepository = assignmentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    /** Checks every minute for pending assignments whose 24-hour reminder window has just arrived. */
    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void remindStudentsAboutUpcomingDeadlines() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.plusHours(24);
        LocalDateTime windowEnd = windowStart.plusMinutes(1);

        assignmentRepository.findAll().stream()
                .filter(assignment -> assignment.getStatus() == AssignmentStatus.PENDING)
                .filter(assignment -> assignment.getDueDate() != null
                        && assignment.getDueDate().isAfter(windowStart)
                        && !assignment.getDueDate().isAfter(windowEnd))
                .forEach(this::notifyEnrolledStudents);
    }

    private void notifyEnrolledStudents(Assignment assignment) {
        if (assignment.getSection() == null || assignment.getSection().getId() == null) {
            return;
        }
        enrollmentRepository.findBySectionId(assignment.getSection().getId()).stream()
                .map(Enrollment::getStudent)
                .filter(student -> student != null)
                .forEach(student -> sendReminder(student, assignment));
    }

    private void sendReminder(Student student, Assignment assignment) {
        // TODO: Send email or push notification to the student.
    }
}
