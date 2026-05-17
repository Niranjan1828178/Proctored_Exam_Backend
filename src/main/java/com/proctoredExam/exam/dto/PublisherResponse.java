package com.proctoredExam.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublisherResponse {
    private Long clientId;
    private String firstname;
    private String lastname;
    private String email;
    private String fullName;
}
