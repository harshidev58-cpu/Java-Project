package com.smartlearn.service;

import com.smartlearn.exception.ResourceNotFoundException;
import com.smartlearn.model.Question;
import com.smartlearn.model.Quiz;
import com.smartlearn.model.QuizAnalytics;
import com.smartlearn.model.QuizAttempt;
import com.smartlearn.repository.QuestionRepository;
import com.smartlearn.repository.QuizAttemptRepository;
import com.smartlearn.repository.QuizRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Builds instructor quiz reports from submitted attempts and quiz questions.
 * This service demonstrates Stream pipelines and Collector-based aggregation for analytics.
 */
@Service
public class QuizAnalyticsService {
    private static final DateTimeFormatter SUBMITTED_AT_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy, h:mm a");

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository attemptRepository;
    private final QuestionRepository questionRepository;

    public QuizAnalyticsService(QuizRepository quizRepository,
                                QuizAttemptRepository attemptRepository,
                                QuestionRepository questionRepository) {
        this.quizRepository = quizRepository;
        this.attemptRepository = attemptRepository;
        this.questionRepository = questionRepository;
    }

    /** Loads submitted results and calculates class and per-question summaries. */
    @Transactional(readOnly = true)
    public QuizAnalytics getAnalytics(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found: " + quizId));
        List<QuizAttempt> attempts = attemptRepository.findByQuiz_IdAndSubmittedAtIsNotNullOrderBySubmittedAtDesc(quizId);
        List<Question> questions = questionRepository.findByQuiz_IdOrderByDisplayOrderAsc(quizId);

        double average = attempts.stream()
                .collect(Collectors.averagingInt(attempt -> attempt.getScore() == null ? 0 : attempt.getScore().intValue()));
        double highest = attempts.stream().mapToDouble(this::score).max().orElse(0);
        double lowest = attempts.stream().mapToDouble(this::score).min().orElse(0);
        long belowForty = attempts.stream().filter(attempt -> score(attempt) < 40).count();

        Map<Long, Question> questionsById = questions.stream()
                .collect(Collectors.toMap(Question::getId, Function.identity()));
        Map<Long, Long> correctCounts = attempts.stream()
                .flatMap(attempt -> attempt.getAnswers().entrySet().stream()
                        .filter(answer -> {
                            Question question = questionsById.get(answer.getKey());
                            return question != null && question.getCorrectAnswer().equals(answer.getValue());
                        })
                        .map(Map.Entry::getKey))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        List<QuizAnalytics.QuestionMetric> metrics = questions.stream()
                .map(question -> {
                    long correct = correctCounts.getOrDefault(question.getId(), 0L);
                    double percentCorrect = attempts.isEmpty() ? 0 : correct * 100.0 / attempts.size();
                    return new QuizAnalytics.QuestionMetric(question.getPrompt(), correct, attempts.size(), percentCorrect);
                })
                .toList();
        QuizAnalytics.QuestionMetric hardest = attempts.isEmpty() ? null : metrics.stream()
                .min((first, second) -> Double.compare(first.getPercentCorrect(), second.getPercentCorrect()))
                .orElse(null);

        List<QuizAnalytics.AttemptRow> rows = attempts.stream()
                .map(attempt -> new QuizAnalytics.AttemptRow(
                        attempt.getStudent().getName(), score(attempt),
                        SUBMITTED_AT_FORMAT.format(attempt.getSubmittedAt())))
                .toList();

        return new QuizAnalytics(quiz.getTitle(), rows, average, highest, lowest, belowForty, metrics, hardest);
    }

    private double score(QuizAttempt attempt) {
        return attempt.getScore() == null ? 0 : attempt.getScore();
    }
}
