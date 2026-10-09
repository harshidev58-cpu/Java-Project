package com.smartlearn.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Signals that a quiz attempt is missing, already submitted, expired, or contains invalid answers.
 * This custom exception maps invalid attempt operations to HTTP 410 Gone.
 */
@ResponseStatus(HttpStatus.GONE)
public class InvalidQuizAttemptException extends RuntimeException {

    public InvalidQuizAttemptException(String message) {
        super(message);
    }
}
