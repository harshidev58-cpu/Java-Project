package com.smartlearn.exception;

/**
 * Signals that a student has no valid section enrollment for a student-specific feature.
 * This exception specializes the existing not-found pattern for enrollment-related failures.
 */
public class EnrollmentNotFoundException extends ResourceNotFoundException {

    public EnrollmentNotFoundException(String message) {
        super(message);
    }
}
