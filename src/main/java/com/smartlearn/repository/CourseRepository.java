package com.smartlearn.repository;

import com.smartlearn.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides database CRUD operations for courses.
 * Spring Data generates the repository implementation at application startup.
 */
public interface CourseRepository extends JpaRepository<Course, Long> {
    /** Checks whether a course code is already present so demo seeding can safely be repeated. */
    boolean existsByCode(String code);
}
