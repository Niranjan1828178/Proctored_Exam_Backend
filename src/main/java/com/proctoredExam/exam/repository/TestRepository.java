package com.proctoredExam.exam.repository;

import com.proctoredExam.exam.entity.ExamTest;
import com.proctoredExam.exam.entity.TestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestRepository extends JpaRepository<ExamTest, Long> {
    List<ExamTest> findByCreatedById(Long clientId);
    List<ExamTest> findByStatus(TestStatus status);
}
