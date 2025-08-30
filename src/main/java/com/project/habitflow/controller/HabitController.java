package com.project.habitflow.controller;

import com.project.habitflow.entity.User;
import com.project.habitflow.mapper.HabitMapper;
import com.project.habitflow.request.HabitRequest;
import com.project.habitflow.response.HabitResponse;
import com.project.habitflow.service.HabitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Habits", description = "Operations related to user habits")
@RestController
@RequestMapping("/api/habits")
public class HabitController {

    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @Operation(summary = "Create a new habit", description = "Create a habit for the authenticated user")
    @PostMapping
    public HabitResponse createHabit(@Valid @RequestBody HabitRequest request,
                                     @AuthenticationPrincipal User user) {
        return HabitMapper.toResponse(habitService.createHabit(
                HabitMapper.toEntity(request), user
        ));
    }

    @Operation(summary = "Get all habits", description = "Retrieve all habits of the authenticated user")
    @GetMapping
    public List<HabitResponse> getHabits(@AuthenticationPrincipal User user) {
        return habitService.getHabits(user).stream()
                .map(HabitMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Update a habit", description = "Update habit details by ID")
    @PutMapping("/{id}")
    public HabitResponse updateHabit(@PathVariable Long id,
                                     @Valid @RequestBody HabitRequest request,
                                     @AuthenticationPrincipal User user) {
        return HabitMapper.toResponse(habitService.updateHabit(
                id, HabitMapper.toEntity(request), user
        ));
    }

    @Operation(summary = "Delete a habit", description = "Delete a habit by ID")
    @DeleteMapping("/{id}")
    public void deleteHabit(@PathVariable Long id, @AuthenticationPrincipal User user) {
        habitService.deleteHabit(id, user);
    }

    @Operation(summary = "Check a habit", description = "Mark a habit as completed for a specific date")
    @PostMapping("/{id}/check")
    public void checkHabit(@PathVariable Long id,
                           @RequestParam LocalDate date,
                           @AuthenticationPrincipal User user) {
        habitService.checkHabit(user, habitService.getHabitById(id, user), date);
    }
}
