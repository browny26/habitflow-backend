package com.project.habitflow.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;

    @Column(nullable = false)
    private String name; // free, premium

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int maxHabits;

    public SubscriptionPlan() {}

    public SubscriptionPlan(String name, double price, int maxHabits) {
        this.name = name;
        this.price = price;
        this.maxHabits = maxHabits;
    }
}
