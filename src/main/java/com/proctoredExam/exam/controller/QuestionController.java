package com.proctoredExam.exam.controller;

import com.proctoredExam.exam.dto.OptionRequest;
import com.proctoredExam.exam.dto.OptionResponse;
import com.proctoredExam.exam.dto.QuestionRequest;
import com.proctoredExam.exam.dto.QuestionResponse;
import com.proctoredExam.exam.entity.User;
import com.proctoredExam.exam.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/tests/{testId}/questions")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<QuestionResponse> addQuestionToTest(
            @PathVariable Long testId,
            @RequestBody QuestionRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(questionService.addQuestionToTest(testId, request, currentUser));
    }

    @PostMapping("/questions/{questionId}/options")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<List<OptionResponse>> addOptionsToQuestion(
            @PathVariable Long questionId,
            @RequestBody List<OptionRequest> requests,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(questionService.addOptionsToQuestion(questionId, requests, currentUser));
    }

    @PutMapping("/questions/{questionId}")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable Long questionId,
            @RequestBody QuestionRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(questionService.updateQuestion(questionId, request, currentUser));
    }

    @DeleteMapping("/questions/{questionId}")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<String> deleteQuestion(
            @PathVariable Long questionId,
            @AuthenticationPrincipal User currentUser
    ) {
        questionService.deleteQuestion(questionId, currentUser);
        return ResponseEntity.ok("Question deleted successfully");
    }
}
