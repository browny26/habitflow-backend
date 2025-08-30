package com.project.habitflow.controller;

import com.project.habitflow.entity.SubscriptionPlan;
import com.project.habitflow.service.SubscriptionPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Subscription Plans", description = "Operations related to subscription plans")
@RestController
@RequestMapping("/api/plans")
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    public SubscriptionPlanController(SubscriptionPlanService subscriptionPlanService) {
        this.subscriptionPlanService = subscriptionPlanService;
    }

    @Operation(summary = "Get all subscription plans", description = "Retrieve a list of all subscription plans")
    @GetMapping
    public List<SubscriptionPlan> getAllPlans() {
        return subscriptionPlanService.getAllPlans();
    }

    @Operation(summary = "Get subscription plan by ID", description = "Retrieve a subscription plan by its ID")
    @GetMapping("/{id}")
    public SubscriptionPlan getPlanById(@PathVariable Long id) {
        return subscriptionPlanService.getPlanById(id)
                .orElseThrow(() -> new RuntimeException("SubscriptionPlan not found with id " + id));
    }

    @Operation(summary = "Create subscription plan", description = "Create a new subscription plan (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionPlan createPlan(@RequestBody SubscriptionPlan plan) {
        return subscriptionPlanService.createPlan(plan);
    }

    @Operation(summary = "Update subscription plan", description = "Update an existing subscription plan by ID (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public SubscriptionPlan updatePlan(@PathVariable Long id, @RequestBody SubscriptionPlan updatedPlan) {
        return subscriptionPlanService.updatePlan(id, updatedPlan);
    }

    @Operation(summary = "Delete subscription plan", description = "Delete a subscription plan by ID (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlan(@PathVariable Long id) {
        subscriptionPlanService.deletePlan(id);
    }
}
