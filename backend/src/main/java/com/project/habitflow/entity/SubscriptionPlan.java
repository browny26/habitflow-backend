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

    @Column(nullable = false)
    private int durationInDays;

    public SubscriptionPlan() {}

    public SubscriptionPlan(String name, double price, int maxHabits, int durationInDays) {
        this.name = name;
        this.price = price;
        this.maxHabits = maxHabits;
        this.durationInDays = durationInDays;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getMaxHabits() {
        return maxHabits;
    }

    public void setMaxHabits(int maxHabits) {
        this.maxHabits = maxHabits;
    }

    public int getDurationInDays() {
        return durationInDays;
    }

    public void setDurationInDays(int durationInDays) {
        this.durationInDays = durationInDays;
    }
}
