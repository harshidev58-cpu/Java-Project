package com.smartlearn.model;

/**
 * Represents a student's submitted work and its grade.
 * This class demonstrates interface implementation by fulfilling the {@link Gradable} contract.
 */
public class AssignmentSubmission implements Gradable {
    private final Long id;
    private final String assignmentName;
    private double grade;

    public AssignmentSubmission(Long id, String assignmentName) {
        this.id = id;
        this.assignmentName = assignmentName;
    }

    public Long getId() {
        return id;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    @Override
    public void assignGrade(double grade) {
        if (grade < 0 || grade > 100) {
            throw new IllegalArgumentException("Grade must be between 0 and 100.");
        }
        this.grade = grade;
    }

    @Override
    public double getGrade() {
        return grade;
    }
}
