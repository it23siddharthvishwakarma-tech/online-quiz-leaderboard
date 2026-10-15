package com.quiz;

import com.quiz.QuizModels.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class QuizController {
    private final QuizService service;

    public QuizController(QuizService service) {
        this.service = service;
    }

    @GetMapping("/health")
    public java.util.Map<String, String> health() {
        return java.util.Map.of("application", "Online Quiz and Leaderboard System", "status", "UP");
    }

    @GetMapping("/quizzes")
    public List<QuizView> quizzes(@RequestParam(required = false) String difficulty) {
        return service.listQuizzes(difficulty);
    }

    @GetMapping("/quizzes/{id}")
    public QuizView quiz(@PathVariable String id) {
        try {
            return service.getQuiz(id);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/attempts")
    public SubmitResponse submit(@RequestBody SubmitRequest request) {
        try {
            return service.submit(request);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/attempts")
    public List<AttemptView> attempts(@RequestParam String student) {
        return service.attemptsFor(student);
    }

    @GetMapping("/leaderboard")
    public List<LeaderboardEntry> leaderboard() {
        return service.leaderboard();
    }
}
