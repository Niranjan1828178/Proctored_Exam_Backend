package com.proctoredExam.exam.service;

import com.proctoredExam.exam.dto.AnswerRequest;
import com.proctoredExam.exam.dto.AnswerResponse;
import com.proctoredExam.exam.entity.*;
import com.proctoredExam.exam.repository.AnswerRepository;
import com.proctoredExam.exam.repository.ExamAttemptRepository;
import com.proctoredExam.exam.repository.OptionRepository;
import com.proctoredExam.exam.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;

    public AnswerResponse saveAnswer(Long attemptId, AnswerRequest request, User currentUser) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        if (!attempt.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to modify this attempt");
        }

        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new RuntimeException("Cannot save answers for a submitted or auto-submitted attempt");
        }

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        if (!question.getTest().getId().equals(attempt.getTest().getId())) {
            throw new RuntimeException("Question does not belong to this test");
        }

        Option selectedOption = null;
        if (request.getSelectedOptionId() != null) {
            selectedOption = optionRepository.findById(request.getSelectedOptionId())
                    .orElseThrow(() -> new RuntimeException("Option not found"));
            
            if (!selectedOption.getQuestion().getId().equals(question.getId())) {
                throw new RuntimeException("Option does not belong to this question");
            }
        }

        Optional<Answer> existingAnswerOpt = answerRepository.findByExamAttemptIdAndQuestionId(attemptId, question.getId());

        Answer answer;
        if (existingAnswerOpt.isPresent()) {
            answer = existingAnswerOpt.get();
            answer.setSelectedOption(selectedOption);
        } else {
            answer = Answer.builder()
                    .examAttempt(attempt)
                    .question(question)
                    .selectedOption(selectedOption)
                    .build();
        }

        answer = answerRepository.save(answer);
        return mapToResponse(answer);
    }

    public List<AnswerResponse> getAnswersForAttempt(Long attemptId, User currentUser) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        // Allow if the user is the owner of the attempt, or if they are an ADMIN/CLIENT who created the test.
        boolean isOwner = attempt.getUser().getId().equals(currentUser.getId());
        boolean isCreator = currentUser.getClientProfile() != null && attempt.getTest().getCreatedBy().getId().equals(currentUser.getClientProfile().getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isCreator && !isAdmin) {
            throw new RuntimeException("Not authorized to view answers for this attempt");
        }

        return answerRepository.findByExamAttemptId(attemptId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AnswerResponse mapToResponse(Answer answer) {
        return AnswerResponse.builder()
                .id(answer.getId())
                .attemptId(answer.getExamAttempt().getId())
                .questionId(answer.getQuestion().getId())
                .selectedOptionId(answer.getSelectedOption() != null ? answer.getSelectedOption().getId() : null)
                .build();
    }
}
