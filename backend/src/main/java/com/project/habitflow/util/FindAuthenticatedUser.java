package com.project.habitflow.util;

import com.project.habitflow.entity.User;
import com.project.habitflow.response.UserResponse;

public interface FindAuthenticatedUser {
    User getAuthenticatedUser();
}
