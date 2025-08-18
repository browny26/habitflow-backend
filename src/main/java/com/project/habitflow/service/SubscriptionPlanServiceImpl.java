package com.project.habitflow.service.impl;

import com.project.habitflow.entity.SubscriptionPlan;
import com.project.habitflow.repository.SubscriptionPlanRepository;
import com.project.habitflow.service.SubscriptionPlanService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public SubscriptionPlanServiceImpl(SubscriptionPlanRepository subscriptionPlanRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    @Override
    public SubscriptionPlan createPlan(SubscriptionPlan plan) {
        return subscriptionPlanRepository.save(plan);
    }

    @Override
    public SubscriptionPlan updatePlan(Long id, SubscriptionPlan updatedPlan) {
        return subscriptionPlanRepository.findById(id)
                .map(plan -> {
                    plan.setName(updatedPlan.getName());
                    plan.setPrice(updatedPlan.getPrice());
                    plan.setDurationInDays(updatedPlan.getDurationInDays());
                    return subscriptionPlanRepository.save(plan);
                })
                .orElseThrow(() -> new RuntimeException("SubscriptionPlan not found with id " + id));
    }

    @Override
    public void deletePlan(Long id) {
        subscriptionPlanRepository.deleteById(id);
    }

    @Override
    public Optional<SubscriptionPlan> getPlanById(Long id) {
        return subscriptionPlanRepository.findById(id);
    }

    @Override
    public List<SubscriptionPlan> getAllPlans() {
        return subscriptionPlanRepository.findAll();
    }
}
