package com.proctoredExam.exam.dto;

import com.proctoredExam.exam.entity.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProctoringLogRequest {
    private EventType eventType;
    private LocalDateTime timestamp;
}
