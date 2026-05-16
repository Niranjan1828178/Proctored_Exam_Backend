package com.proctoredExam.exam.service;

import com.proctoredExam.exam.dto.AuthRequest;
import com.proctoredExam.exam.dto.AuthResponse;
import com.proctoredExam.exam.dto.RegisterRequest;
import com.proctoredExam.exam.dto.UserDto;
import com.proctoredExam.exam.entity.Role;
import com.proctoredExam.exam.entity.User;
import com.proctoredExam.exam.entity.ClientProfile;
import com.proctoredExam.exam.entity.ApprovalStatus;
import com.proctoredExam.exam.repository.UserRepository;
import com.proctoredExam.exam.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        Role assignedRole = Role.USER;
        if (request.getRole() != null && request.getRole().equalsIgnoreCase("CLIENT")) {
            assignedRole = Role.CLIENT;
        } else if (request.getRole() != null && request.getRole().equalsIgnoreCase("ADMIN")) {
            // In a real app, you shouldn't allow users to register as ADMIN directly.
            // But for testing purposes, we allow it.
            assignedRole = Role.ADMIN;
        }

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(assignedRole)
                .build();

        if (assignedRole == Role.CLIENT) {
            ClientProfile profile = ClientProfile.builder()
                    .status(ApprovalStatus.PENDING)
                    .user(user)
                    .build();
            user.setClientProfile(profile);
        }

        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .role(user.getRole().name())
                .approvalStatus(user.getClientProfile() != null ? user.getClientProfile().getStatus() : null)
                .build();

        return AuthResponse.builder()
                .token(jwtToken)
                .user(userDto)
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .role(user.getRole().name())
                .approvalStatus(user.getClientProfile() != null ? user.getClientProfile().getStatus() : null)
                .build();

        return AuthResponse.builder()
                .token(jwtToken)
                .user(userDto)
                .build();
    }
}
