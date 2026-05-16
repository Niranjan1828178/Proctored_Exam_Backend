package com.proctoredExam.exam.repository;

import com.proctoredExam.exam.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findByExamAttemptIdAndQuestionId(Long attemptId, Long questionId);
    List<Answer> findByExamAttemptId(Long attemptId);
}
