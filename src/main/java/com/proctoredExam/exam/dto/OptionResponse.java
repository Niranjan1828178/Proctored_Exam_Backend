package com.proctoredExam.exam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OptionResponse {
    private Long id;
    private String text;
    @JsonProperty("isCorrect")
    private boolean isCorrect;
}
