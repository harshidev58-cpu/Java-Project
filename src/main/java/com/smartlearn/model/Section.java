package com.smartlearn.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Represents one scheduled offering of a course in a semester.
 * This JPA entity models a semester-specific course offering and its related materials, assignments, and enrollments.
 */
@Entity
@Table(name = "sections")
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    private String semester;
    private String syllabus;

    @ElementCollection
    @CollectionTable(name = "section_materials", joinColumns = @JoinColumn(name = "section_id"))
    @jakarta.persistence.Column(name = "material")
    private List<String> materials = new ArrayList<>();

    @OneToMany(mappedBy = "section")
    private List<Assignment> assignments = new ArrayList<>();

    @OneToMany(mappedBy = "section")
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "targetSection")
    private List<Announcement> announcements = new ArrayList<>();

    public Section() {
    }

    public Section(Long id, String name, Course course, String semester) {
        this.id = id;
        this.name = name;
        this.course = course;
        this.semester = semester;
    }

    public Section(Long id, String name, Course course, String semester, String syllabus, List<String> materials) {
        this(id, name, course, semester);
        this.syllabus = syllabus;
        this.materials = materials == null ? new ArrayList<>() : new ArrayList<>(materials);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public String getSyllabus() { return syllabus; }
    public void setSyllabus(String syllabus) { this.syllabus = syllabus; }
    public List<String> getMaterials() { return materials; }
    public void setMaterials(List<String> materials) {
        this.materials = materials == null ? new ArrayList<>() : new ArrayList<>(materials);
    }
    public List<Assignment> getAssignments() { return assignments; }
    public List<Enrollment> getEnrollments() { return enrollments; }
    public List<Announcement> getAnnouncements() { return announcements; }
}
