package com.project.habitflow.service;

import com.project.habitflow.entity.Habit;
import com.project.habitflow.entity.User;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface HabitService {
    Habit createHabit(User user, String name);
    Habit updateHabit(User user, Habit habit);
    void deleteHabit(User user, Habit habit);
    void checkHabit(User user, Habit habit, LocalDate date);
    List<Habit> getHabitsForUser(User user);

}
