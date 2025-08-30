package com.project.habitflow.controller;

import com.project.habitflow.entity.Habit;
import com.project.habitflow.entity.HabitLog;
import com.project.habitflow.entity.User;
import com.project.habitflow.service.HabitLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Tag(name = "Habit Logs", description = "Operations related to logging habits")
@RestController
@RequestMapping("/api/habit-logs")
public class HabitLogController {

    private final HabitLogService habitLogService;

    public HabitLogController(HabitLogService habitLogService) {
        this.habitLogService = habitLogService;
    }

    @Operation(summary = "Log a habit", description = "Log a habit for a specific date")
    @PostMapping
    public HabitLog logHabit(@AuthenticationPrincipal User user,
                             @RequestParam Long habitId,
                             @RequestParam(required = false) LocalDate date) {

        LocalDate logDate = (date != null) ? date : LocalDate.now();
        Habit habit = new Habit();
        habit.setId(habitId);

        return habitLogService.logHabit(user, habit, logDate);
    }

    @Operation(summary = "Get habit log for a date", description = "Get a habit log for a specific habit and date")
    @GetMapping
    public Optional<HabitLog> getHabitLog(@AuthenticationPrincipal User user,
                                          @RequestParam Long habitId,
                                          @RequestParam(required = false) LocalDate date) {

        LocalDate logDate = (date != null) ? date : LocalDate.now();
        Habit habit = new Habit();
        habit.setId(habitId);

        return habitLogService.getHabitLog(user, habit, logDate);
    }

    @Operation(summary = "Get all logs for a habit", description = "Retrieve all logs for a specific habit")
    @GetMapping("/by-habit/{habitId}")
    public List<HabitLog> getLogsByHabit(@PathVariable Long habitId) {
        Habit habit = new Habit();
        habit.setId(habitId);
        return habitLogService.getLogsByHabit(habit);
    }

    @Operation(summary = "Get all logs for the authenticated user", description = "Retrieve all habit logs for the current user")
    @GetMapping("/by-user")
    public List<HabitLog> getLogsByUser(@AuthenticationPrincipal User user) {
        return habitLogService.getLogsByUser(user);
    }

    @Operation(summary = "Delete a habit log", description = "Delete a specific habit log by ID")
    @DeleteMapping("/{logId}")
    public void deleteLog(@PathVariable Long logId) {
        habitLogService.deleteLog(logId);
    }
}
