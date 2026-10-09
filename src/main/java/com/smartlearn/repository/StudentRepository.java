package com.smartlearn.repository;

import com.smartlearn.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Defines data access for students.
 * This interface demonstrates specialization of a generic repository for a concrete model type.
 */
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
}
