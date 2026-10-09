package com.smartlearn.exception;

/**
 * Signals that a requested SmartLearn record does not exist.
 * This custom unchecked exception gives missing-data failures a clear, domain-specific meaning.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
