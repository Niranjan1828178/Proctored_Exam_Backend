package com.proctoredExam.exam.controller;

import com.proctoredExam.exam.dto.ProctoringLogRequest;
import com.proctoredExam.exam.dto.ProctoringLogResponse;
import com.proctoredExam.exam.entity.User;
import com.proctoredExam.exam.service.ProctoringLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attempts/{attemptId}/proctoring-logs")
@RequiredArgsConstructor
public class ProctoringLogController {

    private final ProctoringLogService proctoringLogService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('USER', 'CLIENT', 'ADMIN')")
    public ResponseEntity<ProctoringLogResponse> logEvent(
            @PathVariable Long attemptId,
            @RequestBody ProctoringLogRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(proctoringLogService.logEvent(attemptId, request, currentUser));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'CLIENT', 'ADMIN')")
    public ResponseEntity<List<ProctoringLogResponse>> getLogsForAttempt(
            @PathVariable Long attemptId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(proctoringLogService.getLogsForAttempt(attemptId, currentUser));
    }
}
