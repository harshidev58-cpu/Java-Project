package com.smartlearn.model;

import java.util.List;

/**
 * Holds the instructor-facing quiz report and its calculated metrics.
 * This presentation model separates report data from persistence entities and demonstrates encapsulation.
 */
public class QuizAnalytics {
    private final String quizTitle;
    private final List<AttemptRow> attempts;
    private final double averageScore;
    private final double highestScore;
    private final double lowestScore;
    private final long belowFortyCount;
    private final List<QuestionMetric> questions;
    private final QuestionMetric hardestQuestion;

    public QuizAnalytics(String quizTitle, List<AttemptRow> attempts, double averageScore,
                         double highestScore, double lowestScore, long belowFortyCount,
                         List<QuestionMetric> questions, QuestionMetric hardestQuestion) {
        this.quizTitle = quizTitle;
        this.attempts = List.copyOf(attempts);
        this.averageScore = averageScore;
        this.highestScore = highestScore;
        this.lowestScore = lowestScore;
        this.belowFortyCount = belowFortyCount;
        this.questions = List.copyOf(questions);
        this.hardestQuestion = hardestQuestion;
    }

    public String getQuizTitle() { return quizTitle; }
    public List<AttemptRow> getAttempts() { return attempts; }
    public double getAverageScore() { return averageScore; }
    public double getHighestScore() { return highestScore; }
    public double getLowestScore() { return lowestScore; }
    public long getBelowFortyCount() { return belowFortyCount; }
    public List<QuestionMetric> getQuestions() { return questions; }
    public QuestionMetric getHardestQuestion() { return hardestQuestion; }

    /** A single submitted student's display-ready result row. */
    public static class AttemptRow {
        private final String studentName;
        private final double score;
        private final String submittedAt;

        public AttemptRow(String studentName, double score, String submittedAt) {
            this.studentName = studentName;
            this.score = score;
            this.submittedAt = submittedAt;
        }

        public String getStudentName() { return studentName; }
        public double getScore() { return score; }
        public String getSubmittedAt() { return submittedAt; }
    }

    /** One question's correct-answer count and percentage for this quiz. */
    public static class QuestionMetric {
        private final String prompt;
        private final long correctCount;
        private final int totalAttempts;
        private final double percentCorrect;

        public QuestionMetric(String prompt, long correctCount, int totalAttempts, double percentCorrect) {
            this.prompt = prompt;
            this.correctCount = correctCount;
            this.totalAttempts = totalAttempts;
            this.percentCorrect = percentCorrect;
        }

        public String getPrompt() { return prompt; }
        public long getCorrectCount() { return correctCount; }
        public int getTotalAttempts() { return totalAttempts; }
        public double getPercentCorrect() { return percentCorrect; }
    }
}
