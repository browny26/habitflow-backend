package com.project.habitflow.repository;

import com.project.habitflow.entity.Habit;
import com.project.habitflow.entity.HabitLog;
import com.project.habitflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {
    Optional<HabitLog> findByUserAndHabitAndDate(User user, Habit habit, LocalDate date);

    List<HabitLog> findAllByHabit(Habit habit);

    List<HabitLog> findAllByUser(User user);
}
