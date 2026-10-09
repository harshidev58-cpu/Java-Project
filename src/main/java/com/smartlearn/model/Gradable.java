package com.smartlearn.model;

/**
 * Describes something to which an instructor can assign a numeric grade.
 * This interface demonstrates abstraction through a small behavior contract.
 */
public interface Gradable {
    void assignGrade(double grade);

    double getGrade();
}
