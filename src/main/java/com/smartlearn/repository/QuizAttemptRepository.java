package com.smartlearn.repository;

import com.smartlearn.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Provides database CRUD operations and lookup for a student's quiz attempts.
 * JPQL returns the newest unsubmitted attempt so a page refresh can resume it.
 */
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    /** Returns submitted results for one quiz, newest submission first. */
    List<QuizAttempt> findByQuiz_IdAndSubmittedAtIsNotNullOrderBySubmittedAtDesc(Long quizId);

    @Query("""
            select a from QuizAttempt a
            where a.quiz.id = :quizId and a.student.id = :studentId and a.submittedAt is null
            order by a.startedAt desc
            """)
    List<QuizAttempt> findOpenAttempts(@Param("quizId") Long quizId, @Param("studentId") Long studentId);
}
