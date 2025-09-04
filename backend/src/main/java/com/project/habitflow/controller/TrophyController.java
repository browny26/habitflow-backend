package com.project.habitflow.controller;

import com.project.habitflow.entity.Trophy;
import com.project.habitflow.entity.User;
import com.project.habitflow.entity.UserTrophy;
import com.project.habitflow.response.UserResponse;
import com.project.habitflow.response.UserScoreResponse;
import com.project.habitflow.service.TrophyService;
import com.project.habitflow.service.UserService;
import com.project.habitflow.util.FindAuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Trophies", description = "Operations related to trophies and user scores")
@RestController
@RequestMapping("/api/trophies")
public class TrophyController {

    private final TrophyService trophyService;
    private final FindAuthenticatedUser findAuthenticatedUser;
    private final UserService userService;

    public TrophyController(TrophyService trophyService, FindAuthenticatedUser findAuthenticatedUser, UserService userService) {
        this.trophyService = trophyService;
        this.findAuthenticatedUser = findAuthenticatedUser;
        this.userService = userService;
    }

    @Operation(summary = "Get all trophies", description = "Retrieve a list of all trophies")
    @GetMapping
    public List<Trophy> getAllTrophies() {
        return trophyService.getAllTrophies();
    }

    @Operation(summary = "Get my trophies", description = "Retrieve trophies awarded to the authenticated user")
    @GetMapping("/me")
    public List<UserTrophy> getMyTrophies() {
        User user = findAuthenticatedUser.getAuthenticatedUser();
        return trophyService.getUserTrophies(user);
    }

    @Operation(summary = "Award a trophy manually", description = "Award a specific trophy to the authenticated user")
    @PostMapping("/award/{trophyId}")
    public UserTrophy awardTrophy(@PathVariable Long trophyId) {
        User user = findAuthenticatedUser.getAuthenticatedUser();
        Trophy trophy = trophyService.getAllTrophies().stream()
                .filter(t -> t.getId() == trophyId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Trophy not found"));
        return trophyService.awardTrophy(user, trophy);
    }

    @Operation(summary = "Get my score", description = "Retrieve the total score of the authenticated user")
    @GetMapping("/me/score")
    public UserScoreResponse getMyScore() {
        User user = findAuthenticatedUser.getAuthenticatedUser();
        int score = trophyService.getUserScore(user);
        return new UserScoreResponse(user.getId(), score);
    }

    @Operation(summary = "Add score to user (Admin)", description = "Add points to a user's score (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add-score/{userId}")
    public void addScore(@PathVariable Long userId, @RequestParam int points) {
        // Admin can add points to any user
        User user = userService.getUserEntityById(userId);

        trophyService.addScore(user, points);
    }
}
