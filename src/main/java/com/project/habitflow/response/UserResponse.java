package com.project.habitflow.response;

import com.project.habitflow.entity.User;

import java.util.List;

public class UserResponse {

    private long id;

    private String firstName;

    private String lastName;

    private String email;

    private User.Role role;

    public UserResponse(long id, String firstName, String lastName, String email, User.Role role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
