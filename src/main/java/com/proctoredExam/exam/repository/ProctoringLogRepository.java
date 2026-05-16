package com.proctoredExam.exam.repository;

import com.proctoredExam.exam.entity.ProctoringLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProctoringLogRepository extends JpaRepository<ProctoringLog, Long> {
    List<ProctoringLog> findByExamAttemptIdOrderByTimestampDesc(Long attemptId);
}
