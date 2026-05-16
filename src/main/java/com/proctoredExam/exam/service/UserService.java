package com.proctoredExam.exam.service;

import com.proctoredExam.exam.dto.UserDto;
import com.proctoredExam.exam.entity.User;
import com.proctoredExam.exam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto updateUserProfile(Long userId, UserDto request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        
        if (request.getFirstname() != null) user.setFirstname(request.getFirstname());
        if (request.getLastname() != null) user.setLastname(request.getLastname());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        
        // Explicitly NOT updating role or approval status here to maintain security
        
        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .role(user.getRole().name())
                .approvalStatus(user.getClientProfile() != null ? user.getClientProfile().getStatus() : null)
                .build();
    }
}
