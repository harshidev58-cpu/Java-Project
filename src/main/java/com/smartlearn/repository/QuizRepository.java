package com.smartlearn.repository;

import com.smartlearn.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Provides database CRUD operations for quizzes and a title lookup for idempotent demo seeding.
 * Spring Data generates both the standard operations and the derived query implementation.
 */
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByTitle(String title);
}
