package com.project.habitflow.controller;

import com.project.habitflow.entity.AiFeedback;
import com.project.habitflow.entity.User;
import com.project.habitflow.service.AiFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "AI Habits", description = "Create habits using AI chat suggestions")
@RestController
@RequestMapping("/api/ai")
public class AiFeedbackController {

    private final AiFeedbackService aiFeedbackService;

    public AiFeedbackController(AiFeedbackService aiFeedbackService) {
        this.aiFeedbackService = aiFeedbackService;
    }

    @Operation(summary = "Send input to AI and create Habit", description = "Send a message to AI, get a suggestion, and automatically create a Habit")
    @PostMapping("/chat")
    public AiFeedback sendInput(@AuthenticationPrincipal User user, @RequestParam String message) {
        return aiFeedbackService.sendUserInput(user, message);
    }

    @Operation(summary = "Get previous AI feedbacks", description = "Get all AI responses for the authenticated user")
    @GetMapping("/history")
    public List<AiFeedback> getHistory(@AuthenticationPrincipal User user) {
        return aiFeedbackService.getAllFeedbacksForUser(user);
    }
}
