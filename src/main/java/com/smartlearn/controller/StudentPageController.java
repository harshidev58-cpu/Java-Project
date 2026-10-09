package com.smartlearn.controller;

import com.smartlearn.exception.EnrollmentNotFoundException;
import com.smartlearn.model.Announcement;
import com.smartlearn.model.AnnouncementPriority;
import com.smartlearn.model.Assignment;
import com.smartlearn.model.Enrollment;
import com.smartlearn.model.Section;
import com.smartlearn.model.Student;
import com.smartlearn.repository.EnrollmentRepository;
import com.smartlearn.service.AnnouncementService;
import com.smartlearn.service.AssignmentTrackerService;
import com.smartlearn.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * Prepares server-rendered student pages with data from the domain services.
 * This class demonstrates MVC: it selects a template and supplies its model without embedding business rules in HTML.
 */
@Controller
public class StudentPageController {
    private static final DateTimeFormatter DASHBOARD_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d, yyyy, h:mm a", Locale.ENGLISH);

    private final StudentService studentService;
    private final EnrollmentRepository enrollmentRepository;
    private final AssignmentTrackerService assignmentTrackerService;
    private final AnnouncementService announcementService;

    public StudentPageController(StudentService studentService,
                                 EnrollmentRepository enrollmentRepository,
                                 AssignmentTrackerService assignmentTrackerService,
                                 AnnouncementService announcementService) {
        this.studentService = studentService;
        this.enrollmentRepository = enrollmentRepository;
        this.assignmentTrackerService = assignmentTrackerService;
        this.announcementService = announcementService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam Long studentId, Model model) {
        Student student = studentService.findStudent(studentId);
        List<Enrollment> enrollments = getValidEnrollments(student);
        List<Announcement> relevantAnnouncements = announcementService.getRelevantAnnouncements(student);
        Map<AnnouncementPriority, List<Announcement>> announcementGroups = groupAnnouncements(relevantAnnouncements);

        model.addAttribute("student", student);
        model.addAttribute("enrollments", enrollments);
        List<Assignment> upcomingAssignments = assignmentTrackerService.getUpcomingAssignments(student);
        List<Assignment> overdueAssignments = assignmentTrackerService.getOverdueAssignments(student);
        model.addAttribute("upcomingAssignments", upcomingAssignments);
        model.addAttribute("upcomingDueDates", formatDueDates(upcomingAssignments));
        model.addAttribute("overdueAssignments", overdueAssignments);
        model.addAttribute("overdueDueDates", formatDueDates(overdueAssignments));
        model.addAttribute("announcementGroups", announcementGroups);
        model.addAttribute("announcementPublishedDates", formatPublishedDates(relevantAnnouncements));
        return "dashboard";
    }

    @GetMapping("/course/{sectionId}")
    public String course(@PathVariable Long sectionId, @RequestParam Long studentId, Model model) {
        Student student = studentService.findStudent(studentId);
        Section section = getValidEnrollments(student).stream()
                .map(Enrollment::getSection)
                .filter(enrolledSection -> Objects.equals(enrolledSection.getId(), sectionId))
                .findFirst()
                .orElseThrow(() -> new EnrollmentNotFoundException(
                        "Student is not enrolled in section: " + sectionId));

        List<Announcement> sectionAnnouncements = announcementService.getRelevantAnnouncements(student).stream()
                .filter(announcement -> announcementMatchesSection(announcement, section))
                .toList();
        List<Assignment> sectionAssignments = assignmentTrackerService.getAllAssignments(student).stream()
                .filter(assignment -> assignment.getSection() != null
                        && Objects.equals(assignment.getSection().getId(), sectionId))
                .toList();

        model.addAttribute("student", student);
        model.addAttribute("section", section);
        model.addAttribute("courseAnnouncements", sectionAnnouncements);
        model.addAttribute("courseAssignments", sectionAssignments);
        model.addAttribute("materials", section.getMaterials());
        return "course";
    }

    @GetMapping("/assignments")
    public String assignments(@RequestParam Long studentId, Model model) {
        Student student = studentService.findStudent(studentId);
        model.addAttribute("student", student);
        model.addAttribute("assignments", assignmentTrackerService.getAllAssignments(student));
        return "assignments";
    }

    private List<Enrollment> getValidEnrollments(Student student) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(student.getId()).stream()
                .filter(enrollment -> enrollment.getSection() != null
                        && enrollment.getSection().getId() != null
                        && enrollment.getSection().getCourse() != null)
                .toList();
        if (enrollments.isEmpty()) {
            throw new EnrollmentNotFoundException("Student has no valid course enrollments: " + student.getId());
        }
        return enrollments;
    }

    private Map<AnnouncementPriority, List<Announcement>> groupAnnouncements(List<Announcement> announcements) {
        Map<AnnouncementPriority, List<Announcement>> groups = new LinkedHashMap<>();
        for (AnnouncementPriority priority : AnnouncementPriority.values()) {
            List<Announcement> matching = announcements.stream()
                    .filter(announcement -> announcement.getPriority() == priority)
                    .toList();
            if (!matching.isEmpty()) {
                groups.put(priority, matching);
            }
        }
        return groups;
    }

    private Map<Long, String> formatDueDates(List<Assignment> assignments) {
        return assignments.stream()
                .collect(Collectors.toMap(
                        Assignment::getId,
                        assignment -> assignment.getDueDate() == null
                                ? "Date not set"
                                : DASHBOARD_DATE_FORMAT.format(assignment.getDueDate()),
                        (firstDate, duplicateDate) -> firstDate,
                        LinkedHashMap::new));
    }

    private Map<Long, String> formatPublishedDates(List<Announcement> announcements) {
        return announcements.stream()
                .collect(Collectors.toMap(
                        Announcement::getId,
                        announcement -> announcement.getPublishedAt() == null
                                ? "Date not set"
                                : DASHBOARD_DATE_FORMAT.format(announcement.getPublishedAt()),
                        (firstDate, duplicateDate) -> firstDate,
                        LinkedHashMap::new));
    }

    private boolean announcementMatchesSection(Announcement announcement, Section section) {
        boolean courseMatches = announcement.getTargetCourse() == null
                || Objects.equals(announcement.getTargetCourse().getId(), section.getCourse().getId());
        boolean sectionMatches = announcement.getTargetSection() == null
                || Objects.equals(announcement.getTargetSection().getId(), section.getId());
        boolean semesterMatches = announcement.getTargetSemester() == null
                || Objects.equals(announcement.getTargetSemester(), section.getSemester());
        return courseMatches && sectionMatches && semesterMatches;
    }
}
