package com.project.habitflow.service;

import com.project.habitflow.entity.Habit;
import com.project.habitflow.entity.HabitLog;
import com.project.habitflow.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitLogService {
    HabitLog logHabit(User user, Habit habit, LocalDate date);
    Optional<HabitLog> getHabitLog(User user, Habit habit, LocalDate date);
    List<HabitLog> getLogsByHabit(Habit habit);
    List<HabitLog> getLogsByUser(User user);
    void deleteLog(Long logId);
}
