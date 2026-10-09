package com.smartlearn.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Records one student's saved answers and result for a quiz.
 * This JPA entity persists partial answers separately from the quiz and student records.
 */
@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime submittedAt;
    private Double score;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "quiz_attempt_answers", joinColumns = @JoinColumn(name = "attempt_id"))
    @MapKeyColumn(name = "question_id")
    @Column(name = "selected_answer")
    private Map<Long, String> answers = new HashMap<>();

    protected QuizAttempt() {
    }

    public QuizAttempt(Quiz quiz, Student student, LocalDateTime startedAt, LocalDateTime expiresAt) {
        this.quiz = quiz;
        this.student = student;
        this.startedAt = startedAt;
        this.expiresAt = expiresAt;
    }

    public Long getId() { return id; }
    public Quiz getQuiz() { return quiz; }
    public Student getStudent() { return student; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public Map<Long, String> getAnswers() { return answers; }
}
