package com.proctoredExam.exam.service;

import com.proctoredExam.exam.dto.OptionRequest;
import com.proctoredExam.exam.dto.OptionResponse;
import com.proctoredExam.exam.dto.QuestionRequest;
import com.proctoredExam.exam.dto.QuestionResponse;
import com.proctoredExam.exam.entity.ExamTest;
import com.proctoredExam.exam.entity.Option;
import com.proctoredExam.exam.entity.Question;
import com.proctoredExam.exam.entity.User;
import com.proctoredExam.exam.repository.OptionRepository;
import com.proctoredExam.exam.repository.QuestionRepository;
import com.proctoredExam.exam.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final TestRepository testRepository;

    public QuestionResponse addQuestionToTest(Long testId, QuestionRequest request, User currentUser) {
        ExamTest test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        if (!test.getCreatedBy().getId().equals(currentUser.getClientProfile().getId())) {
            throw new RuntimeException("You are not authorized to add questions to this test");
        }

        Question question = Question.builder()
                .text(request.getText())
                .marks(request.getMarks() != null ? request.getMarks() : 1)
                .test(test)
                .build();

        question = questionRepository.save(question);

        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            long correctCount = request.getOptions().stream().filter(OptionRequest::isCorrect).count();
            if (correctCount > 1) {
                throw new RuntimeException("A question can only have one correct option");
            }

            Question finalQuestion = question;
            List<Option> options = request.getOptions().stream().map(req -> Option.builder()
                    .text(req.getText())
                    .isCorrect(req.isCorrect())
                    .question(finalQuestion)
                    .build()
            ).collect(Collectors.toList());

            options = optionRepository.saveAll(options);
            question.setOptions(options);
        }

        return mapToQuestionResponse(question);
    }

    public List<OptionResponse> addOptionsToQuestion(Long questionId, List<OptionRequest> requests, User currentUser) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        if (!question.getTest().getCreatedBy().getId().equals(currentUser.getClientProfile().getId())) {
            throw new RuntimeException("You are not authorized to add options to this question");
        }

        long correctCount = requests.stream().filter(OptionRequest::isCorrect).count();
        if (question.getOptions() != null) {
            correctCount += question.getOptions().stream().filter(Option::isCorrect).count();
        }

        if (correctCount > 1) {
            throw new RuntimeException("A question can only have one correct option");
        }

        List<Option> options = requests.stream().map(req -> Option.builder()
                .text(req.getText())
                .isCorrect(req.isCorrect())
                .question(question)
                .build()
        ).collect(Collectors.toList());

        options = optionRepository.saveAll(options);

        return options.stream()
                .map(this::mapToOptionResponse)
                .collect(Collectors.toList());
    }

    public QuestionResponse updateQuestion(Long questionId, QuestionRequest request, User currentUser) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        if (!question.getTest().getCreatedBy().getId().equals(currentUser.getClientProfile().getId())) {
            throw new RuntimeException("You are not authorized to update this question");
        }

        question.setText(request.getText());
        if (request.getMarks() != null) {
            question.setMarks(request.getMarks());
        }

        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            long correctCount = request.getOptions().stream().filter(OptionRequest::isCorrect).count();
            if (correctCount > 1) {
                throw new RuntimeException("A question can only have one correct option");
            }

            if (question.getOptions() == null) {
                question.setOptions(new ArrayList<>());
            } else {
                question.getOptions().clear();
            }

            Question finalQuestion = question;
            List<Option> newOptions = request.getOptions().stream().map(req -> Option.builder()
                    .text(req.getText())
                    .isCorrect(req.isCorrect())
                    .question(finalQuestion)
                    .build()
            ).collect(Collectors.toList());

            question.getOptions().addAll(newOptions);
        }

        question = questionRepository.save(question);
        return mapToQuestionResponse(question);
    }

    public void deleteQuestion(Long questionId, User currentUser) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        if (!question.getTest().getCreatedBy().getId().equals(currentUser.getClientProfile().getId())) {
            throw new RuntimeException("You are not authorized to delete this question");
        }

        questionRepository.delete(question);
    }

    public QuestionResponse mapToQuestionResponse(Question question) {
        List<OptionResponse> optionResponses = new ArrayList<>();
        if (question.getOptions() != null) {
            optionResponses = question.getOptions().stream()
                    .map(this::mapToOptionResponse)
                    .collect(Collectors.toList());
        }

        return QuestionResponse.builder()
                .id(question.getId())
                .text(question.getText())
                .marks(question.getMarks())
                .options(optionResponses)
                .build();
    }

    public OptionResponse mapToOptionResponse(Option option) {
        return OptionResponse.builder()
                .id(option.getId())
                .text(option.getText())
                .isCorrect(option.isCorrect())
                .build();
    }
}
