package com.proctoredExam.exam.controller;

import com.proctoredExam.exam.dto.ExamAttemptResponse;
import com.proctoredExam.exam.entity.User;
import com.proctoredExam.exam.service.ExamAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attempts")
@RequiredArgsConstructor
public class ExamAttemptController {

    private final ExamAttemptService examAttemptService;

    @PostMapping("/start/{testId}")
    @PreAuthorize("hasAnyAuthority('USER', 'CLIENT', 'ADMIN')")
    public ResponseEntity<ExamAttemptResponse> startExam(
            @PathVariable Long testId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(examAttemptService.startExam(testId, currentUser));
    }

    @GetMapping("/active/{testId}")
    @PreAuthorize("hasAnyAuthority('USER', 'CLIENT', 'ADMIN')")
    public ResponseEntity<ExamAttemptResponse> getActiveAttempt(
            @PathVariable Long testId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(examAttemptService.getActiveAttempt(testId, currentUser));
    }

    @PostMapping("/{attemptId}/submit")
    @PreAuthorize("hasAnyAuthority('USER', 'CLIENT', 'ADMIN')")
    public ResponseEntity<ExamAttemptResponse> submitExam(
            @PathVariable Long attemptId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(examAttemptService.submitExam(attemptId, currentUser));
    }

    @PostMapping("/{attemptId}/calculate-result")
    @PreAuthorize("hasAnyAuthority('USER', 'CLIENT', 'ADMIN')")
    public ResponseEntity<ExamAttemptResponse> calculateResultExplicitly(
            @PathVariable Long attemptId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(examAttemptService.calculateResultAndReturnResponse(attemptId, currentUser));
    }

    @GetMapping("/my-results")
    @PreAuthorize("hasAnyAuthority('USER', 'CLIENT', 'ADMIN')")
    public ResponseEntity<List<ExamAttemptResponse>> getMyResults(
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(examAttemptService.getMyResults(currentUser));
    }

    @GetMapping("/test/{testId}/results")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN')")
    public ResponseEntity<List<ExamAttemptResponse>> getResultsForTest(
            @PathVariable Long testId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(examAttemptService.getResultsForTest(testId, currentUser));
    }
}
