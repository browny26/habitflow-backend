package com.project.habitflow.service;

import com.project.habitflow.entity.User;
import com.project.habitflow.request.PasswordUpdateRequest;
import com.project.habitflow.request.UserUpdateRequest;
import com.project.habitflow.response.UserResponse;
import com.project.habitflow.response.UserStatsResponse;

import java.util.List;

public interface UserService {
    UserResponse getUserInfo();
    void deleteUser();
    void updatePassword(PasswordUpdateRequest passwordUpdateRequest);
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    UserResponse updateCurrentUser(UserUpdateRequest request);
    UserResponse updateUserById(Long id, UserUpdateRequest request);
    void deleteUserById(Long id);
    UserResponse changeUserRole(Long id, User.Role role);
    List<UserResponse> searchUsersByEmailOrName(String query);
    UserStatsResponse getUserStatistics();
}
