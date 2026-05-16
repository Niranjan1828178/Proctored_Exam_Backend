package com.proctoredExam.exam.service;

import com.proctoredExam.exam.dto.TestRequest;
import com.proctoredExam.exam.dto.TestResponse;
import com.proctoredExam.exam.entity.ExamTest;
import com.proctoredExam.exam.entity.TestStatus;
import com.proctoredExam.exam.entity.User;
import com.proctoredExam.exam.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;

    private final QuestionService questionService;

    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @jakarta.annotation.PostConstruct
    public void fixSchema() {
        try {
            jdbcTemplate.execute("ALTER TABLE exam_test DROP CONSTRAINT IF EXISTS exam_test_status_check");
        } catch(Exception e) {
            System.out.println("Could not drop constraint: " + e.getMessage());
        }
    }

    public TestResponse createTest(TestRequest request, User currentUser) {
        if (currentUser.getClientProfile() == null) {
            throw new RuntimeException("User does not have a client profile");
        }

        ExamTest test = ExamTest.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .duration(request.getDuration())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(request.getStatus() != null ? request.getStatus() : TestStatus.DRAFT)
                .createdBy(currentUser.getClientProfile())
                .build();

        test = testRepository.save(test);
        return mapToResponse(test);
    }

    public TestResponse updateTest(Long testId, TestRequest request, User currentUser) {
        ExamTest test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        if (!test.getCreatedBy().getId().equals(currentUser.getClientProfile().getId())) {
            throw new RuntimeException("You are not authorized to update this test");
        }

        test.setTitle(request.getTitle());
        test.setDescription(request.getDescription());
        test.setDuration(request.getDuration());
        test.setStartTime(request.getStartTime());
        test.setEndTime(request.getEndTime());
        if (request.getStatus() != null) {
            test.setStatus(request.getStatus());
        }

        test = testRepository.save(test);
        return mapToResponse(test);
    }

    public List<TestResponse> getTestsByClient(User currentUser) {
        if (currentUser.getClientProfile() == null) {
            throw new RuntimeException("User does not have a client profile");
        }
        return testRepository.findByCreatedById(currentUser.getClientProfile().getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TestResponse> getAllPublishedTests() {
        return testRepository.findByStatus(TestStatus.PUBLISHED)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TestResponse getTestById(Long testId) {
        ExamTest test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));
        return mapToResponse(test);
    }

    public void deleteTest(Long testId, User currentUser) {
        ExamTest test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");
        boolean isOwner = currentUser.getClientProfile() != null && 
                          test.getCreatedBy().getId().equals(currentUser.getClientProfile().getId());

        if (!isAdmin && !isOwner) {
            throw new RuntimeException("You are not authorized to delete this test");
        }

        test.setStatus(TestStatus.ARCHIVED);
        testRepository.save(test);
    }

    private TestResponse mapToResponse(ExamTest test) {
        java.util.List<com.proctoredExam.exam.dto.QuestionResponse> questionResponses = new java.util.ArrayList<>();
        if (test.getQuestions() != null) {
            questionResponses = test.getQuestions().stream()
                    .map(questionService::mapToQuestionResponse)
                    .collect(Collectors.toList());
        }

        return TestResponse.builder()
                .id(test.getId())
                .title(test.getTitle())
                .description(test.getDescription())
                .duration(test.getDuration())
                .startTime(test.getStartTime())
                .endTime(test.getEndTime())
                .status(test.getStatus())
                .clientId(test.getCreatedBy().getId())
                .questions(questionResponses)
                .build();
    }
}
