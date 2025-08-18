package com.project.habitflow.response;

import com.project.habitflow.entity.User;

import java.util.List;

public class UserResponse {

    private long id;

    private String fullName;

    private String email;

    private User.Role role;

    public UserResponse(long id, String fullName, String email, User.Role role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User.Role getRole() { return role; }

    public void setRole(User.Role role) { this.role = role; }
}
