package com.project.habitflow.controller;

import com.project.habitflow.entity.User;
import com.project.habitflow.repository.UserRepository;
import com.project.habitflow.request.PasswordUpdateRequest;
import com.project.habitflow.request.UserUpdateRequest;
import com.project.habitflow.response.UserResponse;
import com.project.habitflow.response.UserStatsResponse;
import com.project.habitflow.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="User", description = "Operations related to info about current user")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Delete user", description = "Delete current user account")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    public void deleteUser() {
        userService.deleteUser();
    }

    @Operation(summary = "Password update", description = "Change user password after verification")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/password")
    public void passwordUpdate(@Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest)
            throws Exception {
        userService.updatePassword(passwordUpdateRequest);
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user", description = "Update personal information of the logged-in user")
    public UserResponse updateCurrentUser(@RequestBody UserUpdateRequest request) {
        return userService.updateCurrentUser(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Retrieve a list of all users (Admin only)")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by ID", description = "Retrieve user information by user ID (Admin only)")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user (admin)", description = "Update information of a specific user (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        return userService.updateUserById(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user (admin)", description = "Delete a specific user (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
    }

    @PutMapping("/{id}/role")
    @Operation(summary = "Change user role", description = "Change the role of a specific user (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse changeUserRole(@PathVariable Long id, @RequestParam User.Role role) {
        return userService.changeUserRole(id, role);
    }

    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search users by email or name (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> searchUsers(@RequestParam String email) {
        return userService.searchUsersByEmailOrName(email);
    }

    @GetMapping("/stats")
    @Operation(summary = "User statistics", description = "Retrieve statistics about users (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public UserStatsResponse getUserStats() {
        return userService.getUserStatistics();
    }
}
