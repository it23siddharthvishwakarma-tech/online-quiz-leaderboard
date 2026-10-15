package com.quiz;

import java.time.LocalDate;
import java.util.List;

public final class QuizModels {
    private QuizModels() {}

    public record QuestionView(String id, String prompt, List<String> options) {}
    public record QuizView(String id, String title, String topic, String difficulty,
                           List<QuestionView> questions) {}
    public record AnswerSubmission(String questionId, String answer) {}
    public record SubmitRequest(String studentName, String quizId, String difficulty,
                                List<AnswerSubmission> answers) {}
    public record ReviewItem(String questionId, String prompt, String selectedAnswer,
                             String correctAnswer, boolean correct, String explanation) {}
    public record AttemptView(String id, String studentName, String quizTitle,
                              String difficulty, int score, int total,
                              double percentage, LocalDate date, List<ReviewItem> review) {}
    public record LeaderboardEntry(int rank, String studentName, int bestScore,
                                   int totalQuestions, int attempts, int streak) {}
    public record SubmitResponse(AttemptView attempt, List<LeaderboardEntry> leaderboard,
                                 int currentStreak) {}
}
