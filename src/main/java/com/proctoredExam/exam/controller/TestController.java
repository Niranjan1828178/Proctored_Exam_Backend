package com.proctoredExam.exam.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proctoredExam.exam.dto.TestRequest;
import com.proctoredExam.exam.dto.TestResponse;
import com.proctoredExam.exam.entity.User;
import com.proctoredExam.exam.service.TestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<TestResponse> createTest(
            @RequestBody TestRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(testService.createTest(request, currentUser));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<TestResponse> updateTest(
            @PathVariable Long id,
            @RequestBody TestRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(testService.updateTest(id, request, currentUser));
    }

    @GetMapping("/my-tests")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<List<TestResponse>> getMyTests(
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(testService.getTestsByClient(currentUser));
    }

    @GetMapping
    public ResponseEntity<List<TestResponse>> getAllPublishedTests() {
        return ResponseEntity.ok(testService.getAllPublishedTests());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'CLIENT', 'ADMIN')")
    public ResponseEntity<TestResponse> getTestById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(testService.getTestById(id));
    }

    @GetMapping("/{id}/publisher")
    @PreAuthorize("hasAnyAuthority('USER', 'CLIENT', 'ADMIN')")
    public ResponseEntity<com.proctoredExam.exam.dto.PublisherResponse> getPublisherByTestId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(testService.getPublisherByTestId(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN')")
    public ResponseEntity<String> deleteTest(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        testService.deleteTest(id, currentUser);
        return ResponseEntity.ok("Test deleted successfully");
    }
}
