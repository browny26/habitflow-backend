package com.project.habitflow.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "trophies")
public class Trophy {

    public enum Type {
        STREAK,         // basato su giorni consecutivi
        TOTAL_LOGS,     // numero totale di completamenti
        HABIT_CREATED,  // numero di abitudini create
        OTHER           // altro tipo (personalizzato)
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Column(nullable = false)
    private int requirementValue;

    public Trophy() {}

    public Trophy(String name, String description, Type type, int requirementValue) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.requirementValue = requirementValue;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getRequirementValue() {
        return requirementValue;
    }

    public void setRequirementValue(int requirementValue) {
        this.requirementValue = requirementValue;
    }
}
