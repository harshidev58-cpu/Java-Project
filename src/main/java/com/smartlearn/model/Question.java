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
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents one multiple-choice question belonging to a quiz.
 * This JPA entity stores the choices and answer feedback while the many-to-one relationship links it to its quiz.
 */
@Entity
@Table(name = "quiz_questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    private int displayOrder;
    private String prompt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    @OrderColumn(name = "option_order")
    @Column(name = "option_text", nullable = false)
    private List<String> options = new ArrayList<>();

    @Column(nullable = false)
    private String correctAnswer;

    @Column(length = 1000)
    private String explanation;

    protected Question() {
    }

    public Question(Long id, Quiz quiz, int displayOrder, String prompt, List<String> options,
                    String correctAnswer, String explanation) {
        this.id = id;
        this.quiz = quiz;
        this.displayOrder = displayOrder;
        this.prompt = prompt;
        this.options = options == null ? new ArrayList<>() : new ArrayList<>(options);
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
    }

    public Long getId() { return id; }
    public Quiz getQuiz() { return quiz; }
    public int getDisplayOrder() { return displayOrder; }
    public String getPrompt() { return prompt; }
    public List<String> getOptions() { return options; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getExplanation() { return explanation; }
}
