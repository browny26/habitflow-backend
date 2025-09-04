package com.project.habitflow.mapper;

import com.project.habitflow.entity.Habit;
import com.project.habitflow.request.HabitRequest;
import com.project.habitflow.response.HabitResponse;

public class HabitMapper {

    public static HabitResponse toResponse(Habit habit) {
        return new HabitResponse(
                habit.getId(),
                habit.getName(),
                habit.getDescription(),
                habit.isActive(),
                habit.getCreatedAt(),
                habit.getUpdatedAt()
        );
    }

    public static Habit toEntity(HabitRequest request) {
        Habit habit = new Habit();
        habit.setName(request.getName());
        habit.setDescription(request.getDescription());
        if(request.getIsActive() != null) {
            habit.setActive(request.getIsActive());
        }
        return habit;
    }
}
