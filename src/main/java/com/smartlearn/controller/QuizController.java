package com.smartlearn.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlearn.model.Question;
import com.smartlearn.model.QuizAttempt;
import com.smartlearn.service.QuizService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Renders the quiz and exposes autosave and submit endpoints for quiz attempts.
 * This MVC controller passes display data to Thymeleaf and delegates attempt rules to {@link QuizService}.
 */
@Controller
public class QuizController {
    private final QuizService quizService;
    private final ObjectMapper objectMapper;

    public QuizController(QuizService quizService, ObjectMapper objectMapper) {
        this.quizService = quizService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/quiz/{quizId}")
    public String showQuiz(@PathVariable Long quizId, @RequestParam Long studentId, Model model)
            throws JsonProcessingException {
        QuizAttempt attempt = quizService.getOrStartAttempt(quizId, studentId);
        List<Question> questions = quizService.getQuestions(quizId);

        model.addAttribute("quiz", attempt.getQuiz());
        model.addAttribute("attempt", attempt);
        model.addAttribute("student", attempt.getStudent());
        model.addAttribute("questions", questions);
        model.addAttribute("initialAnswersJson", objectMapper.writeValueAsString(attempt.getAnswers()));
        return "quiz";
    }

    @PostMapping(value = "/quiz/{attemptId}/autosave", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> autoSave(@PathVariable Long attemptId,
                                        @RequestBody Map<Long, String> answers) {
        quizService.autoSaveAnswers(attemptId, answers);
        return Map.of("status", "saved");
    }

    @PostMapping(value = "/quiz/{attemptId}/submit", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public QuizService.QuizResult submit(@PathVariable Long attemptId,
                                         @RequestBody Map<Long, String> answers) {
        return quizService.submitAttempt(attemptId, answers);
    }
}
