package com.smartlearn.repository;

import com.smartlearn.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides database CRUD operations for assignment submissions.
 * Spring Data generates the repository implementation at application startup.
 */
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
}
