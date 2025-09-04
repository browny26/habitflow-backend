package com.project.habitflow.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_scores")
public class UserScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    // Constructors, getters, and setters
    public UserScore() {}

    public UserScore(User user, int score) {
        this.user = user;
        this.score = score;
        this.lastUpdated = LocalDateTime.now();
    }

    // Getters and setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}