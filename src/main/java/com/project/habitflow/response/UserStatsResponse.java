package com.project.habitflow.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserStatsResponse {
    private long totalUsers;
    private LocalDate latestUserRegistration;

    public UserStatsResponse(long totalUsers, LocalDate latestUserRegistration) {
        this.totalUsers = totalUsers;
        this.latestUserRegistration = latestUserRegistration;
    }

    // Getters & Setters
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public LocalDate getLatestUserRegistration() { return latestUserRegistration; }
    public void setLatestUserRegistration(LocalDate latestUserRegistration) { this.latestUserRegistration = latestUserRegistration; }
}
