package com.proctoredExam.exam.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.proctoredExam.exam.entity.TestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestRequest {
    private String title;
    private String description;
    private Integer duration;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private TestStatus status;
}
