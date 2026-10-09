package com.smartlearn.model;

import java.time.LocalDateTime;

/**
 * Defines the deadline and lifecycle information needed to track a learning activity.
 * This interface demonstrates abstraction and enables polymorphic tracking of assignments and future exams.
 */
public interface Trackable {
    LocalDateTime getDueDate();

    AssignmentStatus getStatus();
}
