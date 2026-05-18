package com.proctoredExam.exam.dto;

import com.proctoredExam.exam.entity.AttemptStatus;
import com.proctoredExam.exam.entity.QualificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamAttemptResponse {
    private Long id;
    private Long userId;
    private Long testId;
    private String candidateName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AttemptStatus status;
    private Integer score;
    private Integer totalScore;
    private Integer totalQuestions;
    private QualificationStatus qualificationStatus;
}
