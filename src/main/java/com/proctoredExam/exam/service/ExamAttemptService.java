package com.proctoredExam.exam.service;

import com.proctoredExam.exam.dto.ExamAttemptResponse;
import com.proctoredExam.exam.entity.*;
import com.proctoredExam.exam.repository.AnswerRepository;
import com.proctoredExam.exam.repository.ExamAttemptRepository;
import com.proctoredExam.exam.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ExamAttemptService {

    private final ExamAttemptRepository examAttemptRepository;
    private final TestRepository testRepository;
    private final AnswerRepository answerRepository;

    public ExamAttemptResponse startExam(Long testId, User currentUser) {
        ExamTest test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));
        if (test.getStatus() != TestStatus.PUBLISHED) {
            throw new RuntimeException("Test is not published");
        }

        LocalDateTime now = LocalDateTime.now();

        if (test.getStartTime() != null && now.isBefore(test.getStartTime())) {
            throw new RuntimeException("Test has not started yet");
        }

        if (test.getEndTime() != null && now.isAfter(test.getEndTime())) {
            throw new RuntimeException("Test has already ended");
        }

        if (examAttemptRepository.existsByUserIdAndTestId(currentUser.getId(), testId)) {
            throw new RuntimeException("You have already attempted this test");
        }

        ExamAttempt attempt = ExamAttempt.builder()
                .user(currentUser)
                .test(test)
                .startTime(now)
                .status(AttemptStatus.IN_PROGRESS)
                .build();

        attempt = examAttemptRepository.save(attempt);
        return mapToResponse(attempt);
    }

    public ExamAttemptResponse getActiveAttempt(Long testId, User currentUser) {
        ExamAttempt attempt = examAttemptRepository.findByUserIdAndTestIdAndStatus(
                currentUser.getId(), testId, AttemptStatus.IN_PROGRESS
        ).orElseThrow(() -> new RuntimeException("No active attempt found for this test"));

        return mapToResponse(attempt);
    }

    public ExamAttemptResponse submitExam(Long attemptId, User currentUser) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        if (!attempt.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to submit this attempt");
        }

        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new RuntimeException("Attempt is already submitted or not in progress");
        }

        attempt.setStatus(AttemptStatus.SUBMITTED);
        attempt.setEndTime(LocalDateTime.now());

        attempt = examAttemptRepository.save(attempt);
        
        // Calculate result immediately upon submission
        attempt = calculateResult(attemptId);
        
        return mapToResponse(attempt);
    }

    public ExamAttempt calculateResult(Long attemptId) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        java.util.List<Answer> answers = answerRepository.findByExamAttemptId(attemptId);
        int score = 0;

        for (Answer answer : answers) {
            if (answer.getSelectedOption() != null && answer.getSelectedOption().isCorrect()) {
                score += answer.getQuestion().getMarks() != null ? answer.getQuestion().getMarks() : 1;
            }
        }

        int totalQuestions = 0;
        if (attempt.getTest().getQuestions() != null) {
            totalQuestions = attempt.getTest().getQuestions().size();
        }

        attempt.setScore(score);
        attempt.setTotalQuestions(totalQuestions);

        return examAttemptRepository.save(attempt);
    }

    public java.util.List<ExamAttemptResponse> getMyResults(User currentUser) {
        return examAttemptRepository.findByUserIdOrderByStartTimeDesc(currentUser.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    public java.util.List<ExamAttemptResponse> getResultsForTest(Long testId, User currentUser) {
        ExamTest test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        boolean isCreator = currentUser.getClientProfile() != null && test.getCreatedBy().getId().equals(currentUser.getClientProfile().getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isCreator && !isAdmin) {
            throw new RuntimeException("You are not authorized to view results for this test");
        }

        return examAttemptRepository.findByTestIdOrderByScoreDesc(testId)
                .stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    public ExamAttemptResponse calculateResultAndReturnResponse(Long attemptId, User currentUser) {
        ExamAttempt attempt = calculateResult(attemptId);
        // We could also validate currentUser here if needed.
        return mapToResponse(attempt);
    }

    private ExamAttemptResponse mapToResponse(ExamAttempt attempt) {
        return ExamAttemptResponse.builder()
                .id(attempt.getId())
                .userId(attempt.getUser().getId())
                .testId(attempt.getTest().getId())
                .startTime(attempt.getStartTime())
                .endTime(attempt.getEndTime())
                .status(attempt.getStatus())
                .score(attempt.getScore())
                .totalQuestions(attempt.getTotalQuestions())
                .build();
    }
}
