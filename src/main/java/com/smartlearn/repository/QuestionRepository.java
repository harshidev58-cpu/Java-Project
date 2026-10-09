package com.smartlearn.repository;

import com.smartlearn.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Provides database CRUD operations for quiz questions in display order.
 * The derived query follows the question-to-quiz relationship and orders by the explicit sequence field.
 */
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuiz_IdOrderByDisplayOrderAsc(Long quizId);
}
