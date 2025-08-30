package com.project.habitflow.service;

import com.project.habitflow.entity.Habit;
import com.project.habitflow.entity.User;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface HabitService {
    Habit createHabit(Habit habit, User user);
    Habit updateHabit(Long id, Habit updatedHabit, User user);
    void deleteHabit(Long id, User user);
    List<Habit> getHabits(User user);
    void checkHabit(User user, Habit habit, LocalDate date);
    Habit getHabitById(Long id, User user);
}
