package com.smartlearn.service;

import com.smartlearn.exception.InvalidQuizAttemptException;
import com.smartlearn.exception.ResourceNotFoundException;
import com.smartlearn.model.Question;
import com.smartlearn.model.Quiz;
import com.smartlearn.model.QuizAttempt;
import com.smartlearn.model.Student;
import com.smartlearn.repository.QuestionRepository;
import com.smartlearn.repository.QuizAttemptRepository;
import com.smartlearn.repository.QuizRepository;
import com.smartlearn.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Starts attempts, persists partial answers, and grades completed quizzes.
 * This service keeps quiz rules out of the controller and makes grading a server-side responsibility.
 */
@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final StudentRepository studentRepository;

    public QuizService(QuizRepository quizRepository,
                       QuestionRepository questionRepository,
                       QuizAttemptRepository quizAttemptRepository,
                       StudentRepository studentRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.studentRepository = studentRepository;
    }

    /** Finds an unfinished attempt or creates one with the quiz's time limit. */
    @Transactional
    public QuizAttempt getOrStartAttempt(Long quizId, Long studentId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found: " + quizId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        List<QuizAttempt> openAttempts = quizAttemptRepository.findOpenAttempts(quizId, studentId);
        if (!openAttempts.isEmpty()) {
            QuizAttempt attempt = openAttempts.get(0);
            ensureActive(attempt);
            return attempt;
        }

        LocalDateTime startedAt = LocalDateTime.now();
        return quizAttemptRepository.save(new QuizAttempt(
                quiz, student, startedAt, startedAt.plusMinutes(quiz.getDurationMinutes())));
    }

    /** Returns the quiz's questions in their intended display order. */
    @Transactional(readOnly = true)
    public List<Question> getQuestions(Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new ResourceNotFoundException("Quiz not found: " + quizId);
        }
        return questionRepository.findByQuiz_IdOrderByDisplayOrderAsc(quizId);
    }

    /** Merges and persists the latest partial answer set without grading it. */
    @Transactional
    public void autoSaveAnswers(Long attemptId, Map<Long, String> answers) {
        QuizAttempt attempt = getActiveAttempt(attemptId);
        mergeValidatedAnswers(attempt, answers);
        quizAttemptRepository.save(attempt);
    }

    /** Saves the final answers, grades them, marks the attempt submitted, and returns feedback. */
    @Transactional
    public QuizResult submitAttempt(Long attemptId, Map<Long, String> answers) {
        QuizAttempt attempt = getActiveAttempt(attemptId);
        mergeValidatedAnswers(attempt, answers);

        List<Question> questions = questionRepository.findByQuiz_IdOrderByDisplayOrderAsc(attempt.getQuiz().getId());
        if (questions.isEmpty()) {
            throw new InvalidQuizAttemptException("This quiz has no questions to grade.");
        }

        int correctCount = 0;
        List<QuestionFeedback> feedback = new ArrayList<>();
        for (Question question : questions) {
            String selectedAnswer = attempt.getAnswers().get(question.getId());
            boolean correct = question.getCorrectAnswer().equals(selectedAnswer);
            if (correct) {
                correctCount++;
            }
            feedback.add(new QuestionFeedback(question.getId(), question.getPrompt(), selectedAnswer,
                    question.getCorrectAnswer(), correct, question.getExplanation()));
        }

        int score = Math.round(correctCount * 100.0f / questions.size());
        attempt.setScore((double) score);
        attempt.setSubmittedAt(LocalDateTime.now());
        quizAttemptRepository.save(attempt);
        return new QuizResult(attempt.getId(), score, correctCount, questions.size(), feedback);
    }

    private QuizAttempt getActiveAttempt(Long attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new InvalidQuizAttemptException("Quiz attempt not found: " + attemptId));
        ensureActive(attempt);
        return attempt;
    }

    private void ensureActive(QuizAttempt attempt) {
        if (attempt.getSubmittedAt() != null) {
            throw new InvalidQuizAttemptException("This quiz attempt has already been submitted.");
        }
        if (attempt.getExpiresAt() == null || !attempt.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new InvalidQuizAttemptException("This quiz attempt has expired.");
        }
    }

    private void mergeValidatedAnswers(QuizAttempt attempt, Map<Long, String> submittedAnswers) {
        Map<Long, String> answers = submittedAnswers == null ? Map.of() : submittedAnswers;
        List<Question> questions = questionRepository.findByQuiz_IdOrderByDisplayOrderAsc(attempt.getQuiz().getId());
        Map<Long, Question> questionsById = new HashMap<>();
        questions.forEach(question -> questionsById.put(question.getId(), question));

        answers.forEach((questionId, selectedOption) -> {
            Question question = questionsById.get(questionId);
            if (question == null) {
                throw new InvalidQuizAttemptException("Question does not belong to this quiz: " + questionId);
            }
            if (selectedOption != null && !question.getOptions().contains(selectedOption)) {
                throw new InvalidQuizAttemptException("Selected option is invalid for question: " + questionId);
            }
        });
        attempt.getAnswers().putAll(answers);
    }

    /** One question's grading outcome and feedback returned after submission. */
    public record QuestionFeedback(Long questionId, String prompt, String selectedAnswer,
                                   String correctAnswer, boolean correct, String explanation) {
    }

    /** Overall score plus the per-question feedback used by the quiz page. */
    public record QuizResult(Long attemptId, int score, int correctCount, int totalQuestions,
                             List<QuestionFeedback> feedback) {
    }
}
