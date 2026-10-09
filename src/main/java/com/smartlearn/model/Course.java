package com.smartlearn.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a course offered through SmartLearn.
 * This JPA entity groups course data and exposes its sections, assignments, and announcements through relationships.
 */
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String title;
    private String description;

    @OneToMany(mappedBy = "course")
    private List<Section> sections = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<Assignment> assignments = new ArrayList<>();

    @OneToMany(mappedBy = "targetCourse")
    private List<Announcement> announcements = new ArrayList<>();

    public Course() {
    }

    public Course(Long id, String code, String title, String description) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Section> getSections() { return sections; }
    public List<Assignment> getAssignments() { return assignments; }
    public List<Announcement> getAnnouncements() { return announcements; }
}
