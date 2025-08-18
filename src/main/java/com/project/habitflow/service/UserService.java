package com.project.habitflow.service;

import com.project.habitflow.request.PasswordUpdateRequest;
import com.project.habitflow.response.UserResponse;

public interface UserService {
    UserResponse getUserInfo();
    void deleteUser();
    void updatePassword(PasswordUpdateRequest passwordUpdateRequest);
}
