package com.proctoredExam.exam.controller;

import com.proctoredExam.exam.dto.UserDto;
import com.proctoredExam.exam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateProfile(@PathVariable Long id, @RequestBody UserDto request) {
        return ResponseEntity.ok(userService.updateUserProfile(id, request));
    }
}
