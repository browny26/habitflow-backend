package com.project.habitflow.service;

import com.project.habitflow.entity.Habit;
import com.project.habitflow.entity.HabitLog;
import com.project.habitflow.entity.User;
import com.project.habitflow.repository.HabitLogRepository;
import com.project.habitflow.repository.HabitRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class HabitServiceImpl implements HabitService {

    private final HabitLogRepository habitLogRepository;
    private final HabitRepository habitRepository;

    public HabitServiceImpl(HabitLogRepository habitLogRepository, HabitRepository habitRepository) {
        this.habitLogRepository = habitLogRepository;
        this.habitRepository = habitRepository;
    }

    @Override
    public Habit createHabit(Habit habit, User user) {
        habit.setUser(user); // collega l’habit all’utente autenticato
        return habitRepository.save(habit);
    }

    @Override
    public Habit updateHabit(Long id, Habit updatedHabit, User user) {
        Habit existingHabit = habitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        if (!existingHabit.getUser().equals(user)) {
            throw new AccessDeniedException("This habit doesn't belong to the user");
        }

        // aggiorna solo i campi modificabili
        existingHabit.setName(updatedHabit.getName());
        existingHabit.setDescription(updatedHabit.getDescription());
        existingHabit.setActive(updatedHabit.isActive());

        return habitRepository.save(existingHabit);
    }

    @Override
    public void deleteHabit(Long id, User user) {
        Habit existingHabit = habitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        if (!existingHabit.getUser().equals(user)) {
            throw new AccessDeniedException("This habit doesn't belong to the user");
        }

        habitRepository.delete(existingHabit);
    }

    @Override
    public List<Habit> getHabits(User user) {
        return habitRepository.findByUser(user);
    }

    @Override
    public void checkHabit(User user, Habit habit, LocalDate date) {
        if (!habit.getUser().equals(user)) {
            throw new AccessDeniedException("This habit doesn't belong to the user");
        }

        Optional<HabitLog> log = habitLogRepository.findByUserAndHabitAndDate(user, habit, date);

        if (log.isPresent()) {
            // Habit già completato per questa data
            System.out.println("Habit già completato per la data: " + date);
        } else {
            HabitLog newLog = new HabitLog(user, habit, date);
            habitLogRepository.save(newLog);
        }
    }

    @Override
    public Habit getHabitById(Long id, User user) {
        Habit habit = habitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        if (!habit.getUser().equals(user)) {
            throw new AccessDeniedException("This habit doesn't belong to the user");
        }

        return habit;
    }

}
