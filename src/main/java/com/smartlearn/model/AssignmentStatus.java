package com.smartlearn.model;

/**
 * Describes the current lifecycle state of an assignment.
 * This enum demonstrates a type-safe set of allowed status values.
 */
public enum AssignmentStatus {
    PENDING,
    SUBMITTED,
    GRADED,
    OVERDUE
}
