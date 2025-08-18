package com.project.habitflow.service.impl;

import com.project.habitflow.entity.Habit;
import com.project.habitflow.entity.HabitLog;
import com.project.habitflow.entity.User;
import com.project.habitflow.repository.HabitLogRepository;
import com.project.habitflow.service.HabitLogService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class HabitLogServiceImpl implements HabitLogService {

    private final HabitLogRepository habitLogRepository;

    public HabitLogServiceImpl(HabitLogRepository habitLogRepository) {
        this.habitLogRepository = habitLogRepository;
    }

    @Override
    public HabitLog logHabit(User user, Habit habit, LocalDate date) {
        if (!habit.getUser().equals(user)) {
            throw new AccessDeniedException("This habit doesn't belong to the user");
        }

        Optional<HabitLog> existingLog = habitLogRepository.findByUserAndHabitAndDate(user, habit, date);

        if (existingLog.isPresent()) {
            return existingLog.get(); // già loggato, ritorna il log esistente
        }

        HabitLog newLog = new HabitLog(user, habit, date);
        return habitLogRepository.save(newLog);
    }

    @Override
    public Optional<HabitLog> getHabitLog(User user, Habit habit, LocalDate date) {
        return habitLogRepository.findByUserAndHabitAndDate(user, habit, date);
    }

    @Override
    public List<HabitLog> getLogsByHabit(Habit habit) {
        return habitLogRepository.findAllByHabit(habit);
    }

    @Override
    public List<HabitLog> getLogsByUser(User user) {
        return habitLogRepository.findAllByUser(user);
    }

    @Override
    public void deleteLog(Long logId) {
        habitLogRepository.deleteById(logId);
    }
}
