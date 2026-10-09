package com.smartlearn.controller;

import com.smartlearn.model.QuizAnalytics;
import com.smartlearn.service.QuizAnalyticsService;
import com.smartlearn.service.QuizExportService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Serves instructors their quiz report page and Excel download.
 * This MVC controller demonstrates routing and delegates analytics and file creation to services.
 */
@Controller
public class InstructorController {
    private static final MediaType XLSX_MEDIA_TYPE =
            MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final QuizAnalyticsService analyticsService;
    private final QuizExportService exportService;

    public InstructorController(QuizAnalyticsService analyticsService, QuizExportService exportService) {
        this.analyticsService = analyticsService;
        this.exportService = exportService;
    }

    /** Renders the submitted attempt list and class summary for a quiz. */
    @GetMapping("/instructor/quiz/{quizId}/results")
    public String results(@PathVariable Long quizId, Model model) {
        QuizAnalytics analytics = analyticsService.getAnalytics(quizId);
        model.addAttribute("analytics", analytics);
        model.addAttribute("quizId", quizId);
        return "results";
    }

    /** Sends an XLSX attachment with student results and question performance. */
    @GetMapping("/instructor/quiz/{quizId}/export")
    public ResponseEntity<byte[]> export(@PathVariable Long quizId) {
        byte[] workbook = exportService.exportResults(quizId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(XLSX_MEDIA_TYPE);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("quiz-" + quizId + "-results.xlsx").build());
        headers.setContentLength(workbook.length);
        return ResponseEntity.ok().headers(headers).body(workbook);
    }
}
