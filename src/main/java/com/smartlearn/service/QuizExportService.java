package com.smartlearn.service;

import com.smartlearn.model.QuizAnalytics;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Converts quiz analytics into a two-sheet Excel workbook.
 * This service demonstrates separation of concerns by keeping spreadsheet formatting outside the web controller.
 */
@Service
public class QuizExportService {
    private final QuizAnalyticsService analyticsService;

    public QuizExportService(QuizAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /** Creates the downloadable XLSX bytes for one quiz's submitted results. */
    public byte[] exportResults(Long quizId) {
        QuizAnalytics analytics = analyticsService.getAnalytics(quizId);
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet results = workbook.createSheet("Results");
            Row resultsHeader = results.createRow(0);
            resultsHeader.createCell(0).setCellValue("Student");
            resultsHeader.createCell(1).setCellValue("Score (%)");
            resultsHeader.createCell(2).setCellValue("Submitted at");
            for (int index = 0; index < analytics.getAttempts().size(); index++) {
                QuizAnalytics.AttemptRow attempt = analytics.getAttempts().get(index);
                Row row = results.createRow(index + 1);
                row.createCell(0).setCellValue(attempt.getStudentName());
                row.createCell(1).setCellValue(attempt.getScore());
                row.createCell(2).setCellValue(attempt.getSubmittedAt());
            }
            sizeColumns(results, 3);

            Sheet questionAnalysis = workbook.createSheet("Question Analysis");
            Row questionHeader = questionAnalysis.createRow(0);
            questionHeader.createCell(0).setCellValue("Question");
            questionHeader.createCell(1).setCellValue("Correct answers");
            questionHeader.createCell(2).setCellValue("Submitted attempts");
            questionHeader.createCell(3).setCellValue("Correct (%)");
            for (int index = 0; index < analytics.getQuestions().size(); index++) {
                QuizAnalytics.QuestionMetric metric = analytics.getQuestions().get(index);
                Row row = questionAnalysis.createRow(index + 1);
                row.createCell(0).setCellValue(metric.getPrompt());
                row.createCell(1).setCellValue(metric.getCorrectCount());
                row.createCell(2).setCellValue(metric.getTotalAttempts());
                row.createCell(3).setCellValue(metric.getPercentCorrect());
            }
            sizeColumns(questionAnalysis, 4);

            workbook.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not create the quiz results workbook.", exception);
        }
    }

    private void sizeColumns(Sheet sheet, int columnCount) {
        for (int column = 0; column < columnCount; column++) {
            sheet.autoSizeColumn(column);
        }
    }
}
