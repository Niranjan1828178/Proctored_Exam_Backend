package com.proctoredExam.exam.repository;

import com.proctoredExam.exam.entity.AttemptStatus;
import com.proctoredExam.exam.entity.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {

    Optional<ExamAttempt> findByUserIdAndTestId(Long userId, Long testId);

    Optional<ExamAttempt> findByUserIdAndTestIdAndStatus(Long userId, Long testId, AttemptStatus status);

    boolean existsByUserIdAndTestId(Long userId, Long testId);

    List<ExamAttempt> findByUserIdOrderByStartTimeDesc(Long userId);

    List<ExamAttempt> findByTestIdOrderByScoreDesc(Long testId);
}
