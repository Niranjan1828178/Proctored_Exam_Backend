package com.proctoredExam.exam.service;

import com.proctoredExam.exam.dto.ProctoringLogRequest;
import com.proctoredExam.exam.dto.ProctoringLogResponse;
import com.proctoredExam.exam.entity.*;
import com.proctoredExam.exam.repository.ExamAttemptRepository;
import com.proctoredExam.exam.repository.ProctoringLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProctoringLogService {

    private final ProctoringLogRepository proctoringLogRepository;
    private final ExamAttemptRepository examAttemptRepository;

    public ProctoringLogResponse logEvent(Long attemptId, ProctoringLogRequest request, User currentUser) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        if (!attempt.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to log events for this attempt");
        }

        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new RuntimeException("Cannot log events for a submitted attempt");
        }

        LocalDateTime timestamp = request.getTimestamp() != null ? request.getTimestamp() : LocalDateTime.now();

        ProctoringLog log = ProctoringLog.builder()
                .examAttempt(attempt)
                .eventType(request.getEventType())
                .timestamp(timestamp)
                .build();

        log = proctoringLogRepository.save(log);
        return mapToResponse(log);
    }

    public List<ProctoringLogResponse> getLogsForAttempt(Long attemptId, User currentUser) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        boolean isOwner = attempt.getUser().getId().equals(currentUser.getId());
        boolean isCreator = currentUser.getClientProfile() != null && attempt.getTest().getCreatedBy().getId().equals(currentUser.getClientProfile().getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isCreator && !isAdmin) {
            throw new RuntimeException("Not authorized to view logs for this attempt");
        }

        return proctoringLogRepository.findByExamAttemptIdOrderByTimestampDesc(attemptId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ProctoringLogResponse mapToResponse(ProctoringLog log) {
        return ProctoringLogResponse.builder()
                .id(log.getId())
                .attemptId(log.getExamAttempt().getId())
                .eventType(log.getEventType())
                .timestamp(log.getTimestamp())
                .build();
    }
}
