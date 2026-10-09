package com.smartlearn.model;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Represents a message targeted to a course, section, and/or semester.
 * This JPA entity carries the targeting and ordering data used by announcement queries, with optional course and section relationships.
 */
@Entity
@Table(name = "announcements")
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_course_id")
    private Course targetCourse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_section_id")
    private Section targetSection;
    private String targetSemester;

    @Enumerated(EnumType.STRING)
    private AnnouncementPriority priority;
    private LocalDateTime publishedAt;

    public Announcement() {
    }

    public Announcement(Long id, String title, String message, Course targetCourse, Section targetSection,
                        String targetSemester, AnnouncementPriority priority, LocalDateTime publishedAt) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.targetCourse = targetCourse;
        this.targetSection = targetSection;
        this.targetSemester = targetSemester;
        this.priority = priority;
        this.publishedAt = publishedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Course getTargetCourse() { return targetCourse; }
    public void setTargetCourse(Course targetCourse) { this.targetCourse = targetCourse; }
    public Section getTargetSection() { return targetSection; }
    public void setTargetSection(Section targetSection) { this.targetSection = targetSection; }
    public String getTargetSemester() { return targetSemester; }
    public void setTargetSemester(String targetSemester) { this.targetSemester = targetSemester; }
    public AnnouncementPriority getPriority() { return priority; }
    public void setPriority(AnnouncementPriority priority) { this.priority = priority; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
}
