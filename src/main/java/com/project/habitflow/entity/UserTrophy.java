package com.project.habitflow.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "user_trophies", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "trophy_id"})
})
public class UserTrophy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trophy_id")
    private Trophy trophy;

    @CreationTimestamp
    @Column(updatable = false, name = "unlocked_at")
    private Date unlockedAt;

    public UserTrophy() {}

    public UserTrophy(User user, Trophy trophy, Date unlockedAt) {
        this.user = user;
        this.trophy = trophy;
        this.unlockedAt = unlockedAt;
    }


}
