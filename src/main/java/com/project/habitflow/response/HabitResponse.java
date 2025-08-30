package com.project.habitflow.response;

import java.time.LocalDate;

public class HabitResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    // Costruttore
    public HabitResponse(Long id, String name, String description, Boolean isActive, LocalDate createdAt, LocalDate updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Boolean getIsActive() { return isActive; }
    public LocalDate getCreatedAt() { return createdAt; }
    public LocalDate getUpdatedAt() { return updatedAt; }
}
