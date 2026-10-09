package com.smartlearn.service;

import com.smartlearn.exception.InvalidQuizAttemptException;
import com.smartlearn.model.Question;
import com.smartlearn.model.Quiz;
import com.smartlearn.model.QuizAttempt;
import com.smartlearn.model.Student;
import com.smartlearn.repository.QuestionRepository;
import com.smartlearn.repository.QuizAttemptRepository;
import com.smartlearn.repository.QuizRepository;
import com.smartlearn.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Checks quiz scoring and expiration behavior with isolated repository mocks.
 * These JUnit 5 tests demonstrate unit testing and Mockito-based dependency substitution.
 */
@ExtendWith(MockitoExtension.class)
class QuizServiceTest {
    @Mock private QuizRepository quizRepository;
    @Mock private QuestionRepository questionRepository;
    @Mock private QuizAttemptRepository attemptRepository;
    @Mock private StudentRepository studentRepository;

    private QuizService service;

    @BeforeEach
    void setUp() {
        service = new QuizService(quizRepository, questionRepository, attemptRepository, studentRepository);
    }

    @Test
    void submitAttemptScoresAllCorrectAnswersAsOneHundred() {
        stubGradingRepositories();
        QuizAttempt attempt = activeAttempt();
        when(attemptRepository.findById(5L)).thenReturn(Optional.of(attempt));

        QuizService.QuizResult result = service.submitAttempt(5L, Map.of(101L, "A", 102L, "B"));

        assertEquals(100, result.score());
        assertEquals(2, result.correctCount());
    }

    @Test
    void submitAttemptScoresAllWrongAnswersAsZero() {
        stubGradingRepositories();
        QuizAttempt attempt = activeAttempt();
        when(attemptRepository.findById(5L)).thenReturn(Optional.of(attempt));

        QuizService.QuizResult result = service.submitAttempt(5L, Map.of(101L, "B", 102L, "A"));

        assertEquals(0, result.score());
        assertEquals(0, result.correctCount());
    }

    @Test
    void submitAttemptScoresPartialAnswersByCorrectQuestionCount() {
        stubGradingRepositories();
        QuizAttempt attempt = activeAttempt();
        when(attemptRepository.findById(5L)).thenReturn(Optional.of(attempt));

        QuizService.QuizResult result = service.submitAttempt(5L, Map.of(101L, "A", 102L, "A"));

        assertEquals(50, result.score());
        assertEquals(1, result.correctCount());
    }

    @Test
    void submitAttemptThrowsWhenAttemptIsExpired() {
        QuizAttempt attempt = activeAttempt();
        attempt = new QuizAttempt(attempt.getQuiz(), attempt.getStudent(), LocalDateTime.now().minusHours(1),
                LocalDateTime.now().minusMinutes(1));
        when(attemptRepository.findById(5L)).thenReturn(Optional.of(attempt));

        assertThrows(InvalidQuizAttemptException.class, () -> service.submitAttempt(5L, Map.of()));
    }

    private QuizAttempt activeAttempt() {
        Quiz quiz = new Quiz(7L, "Test Quiz", "", 30);
        return new QuizAttempt(quiz, new Student(9L, "Test Student", "student@example.edu"),
                LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(30));
    }

    private void stubGradingRepositories() {
        List<Question> questions = List.of(
                question(101L, "A", "Question one"),
                question(102L, "B", "Question two"));
        when(questionRepository.findByQuiz_IdOrderByDisplayOrderAsc(7L)).thenReturn(questions);
        when(attemptRepository.save(any(QuizAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private Question question(Long id, String correct, String prompt) {
        Question question = mock(Question.class);
        when(question.getId()).thenReturn(id);
        when(question.getCorrectAnswer()).thenReturn(correct);
        when(question.getPrompt()).thenReturn(prompt);
        when(question.getExplanation()).thenReturn("Explanation");
        when(question.getOptions()).thenReturn(List.of("A", "B"));
        return question;
    }
}
