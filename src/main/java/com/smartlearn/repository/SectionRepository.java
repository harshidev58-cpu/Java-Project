package com.smartlearn.repository;

import com.smartlearn.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides database CRUD operations for course sections.
 * Spring Data generates the repository implementation at application startup.
 */
public interface SectionRepository extends JpaRepository<Section, Long> {
}
