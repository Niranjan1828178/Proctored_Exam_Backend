package com.proctoredExam.exam.service;

import com.proctoredExam.exam.dto.UserDto;
import com.proctoredExam.exam.entity.ApprovalStatus;
import com.proctoredExam.exam.entity.Role;
import com.proctoredExam.exam.entity.User;
import com.proctoredExam.exam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public void approveClient(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole() == Role.CLIENT && user.getClientProfile() != null) {
            user.getClientProfile().setStatus(ApprovalStatus.APPROVED);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User is not a CLIENT or profile missing");
        }
    }

    public void rejectClient(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole() == Role.CLIENT && user.getClientProfile() != null) {
            user.getClientProfile().setStatus(ApprovalStatus.REJECTED);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User is not a CLIENT or profile missing");
        }
    }

    public void updateUser(Long userId, UserDto request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (request.getFirstname() != null) user.setFirstname(request.getFirstname());
        if (request.getLastname() != null) user.setLastname(request.getLastname());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getRole() != null) user.setRole(Role.valueOf(request.getRole()));
        userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    private UserDto mapToDto(User user) {
        ApprovalStatus status = null;
        if (user.getClientProfile() != null) {
            status = user.getClientProfile().getStatus();
        }
        return UserDto.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .role(user.getRole().name())
                .approvalStatus(status)
                .build();
    }
}
