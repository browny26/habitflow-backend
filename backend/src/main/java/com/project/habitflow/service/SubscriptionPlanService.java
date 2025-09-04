package com.project.habitflow.service;

import com.project.habitflow.entity.SubscriptionPlan;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanService {
    SubscriptionPlan createPlan(SubscriptionPlan plan);
    SubscriptionPlan updatePlan(Long id, SubscriptionPlan updatedPlan);
    void deletePlan(Long id);
    Optional<SubscriptionPlan> getPlanById(Long id);
    List<SubscriptionPlan> getAllPlans();
}
