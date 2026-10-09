package com.smartlearn.model;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Links a student to the section in which they are enrolled.
 * This JPA entity models a student-to-section relationship, allowing enrollment-specific data such as its date and progress.
 */
@Entity
@Table(name = "enrollments")
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;
    private LocalDate enrolledOn;
    private int progressPercent;

    public Enrollment() {
    }

    public Enrollment(Long id, Student student, Section section, LocalDate enrolledOn) {
        this.id = id;
        this.student = student;
        this.section = section;
        this.enrolledOn = enrolledOn;
    }

    public Enrollment(Long id, Student student, Section section, LocalDate enrolledOn, int progressPercent) {
        this(id, student, section, enrolledOn);
        setProgressPercent(progressPercent);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public Section getSection() { return section; }
    public void setSection(Section section) { this.section = section; }
    public LocalDate getEnrolledOn() { return enrolledOn; }
    public void setEnrolledOn(LocalDate enrolledOn) { this.enrolledOn = enrolledOn; }
    public int getProgressPercent() { return progressPercent; }
    public void setProgressPercent(int progressPercent) {
        if (progressPercent < 0 || progressPercent > 100) {
            throw new IllegalArgumentException("Progress must be between 0 and 100.");
        }
        this.progressPercent = progressPercent;
    }
}
