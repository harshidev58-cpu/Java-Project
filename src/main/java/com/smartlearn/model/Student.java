package com.smartlearn.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a learner enrolled in SmartLearn.
 * This class demonstrates inheritance by reusing the shared fields and behavior in {@link User},
 * and is the persisted JPA entity used by student-facing features.
 */
@Entity
@Table(name = "students")
public class Student extends User {
    @OneToMany(mappedBy = "student")
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    private List<Submission> submissions = new ArrayList<>();

    protected Student() {
        super();
    }

    public Student(Long id, String name, String email) {
        super(id, name, email);
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    @Override
    public Role getRole() {
        return Role.STUDENT;
    }
}
