package com.proctoredExam.exam.dto;

import com.proctoredExam.exam.entity.QualificationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamAttemptQualificationRequest {
    private QualificationStatus qualificationStatus;
}
