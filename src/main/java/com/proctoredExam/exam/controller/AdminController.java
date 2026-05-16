package com.proctoredExam.exam.controller;

import com.proctoredExam.exam.dto.UserDto;
import com.proctoredExam.exam.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/clients/{id}/approve")
    public ResponseEntity<String> approveClient(@PathVariable Long id) {
        adminService.approveClient(id);
        return ResponseEntity.ok("Client approved successfully");
    }

    @PutMapping("/clients/{id}/reject")
    public ResponseEntity<String> rejectClient(@PathVariable Long id) {
        adminService.rejectClient(id);
        return ResponseEntity.ok("Client rejected successfully");
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody UserDto request) {
        adminService.updateUser(id, request);
        return ResponseEntity.ok("User updated successfully");
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
