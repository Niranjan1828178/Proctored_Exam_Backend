package com.proctoredExam.exam.controller;

import com.proctoredExam.exam.dto.AnswerRequest;
import com.proctoredExam.exam.dto.AnswerResponse;
import com.proctoredExam.exam.entity.User;
import com.proctoredExam.exam.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attempts/{attemptId}/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('USER', 'CLIENT', 'ADMIN')")
    public ResponseEntity<AnswerResponse> saveAnswer(
            @PathVariable Long attemptId,
            @RequestBody AnswerRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(answerService.saveAnswer(attemptId, request, currentUser));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'CLIENT', 'ADMIN')")
    public ResponseEntity<List<AnswerResponse>> getAnswersForAttempt(
            @PathVariable Long attemptId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(answerService.getAnswersForAttempt(attemptId, currentUser));
    }
}
