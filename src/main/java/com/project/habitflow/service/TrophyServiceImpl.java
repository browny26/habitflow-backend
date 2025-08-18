package com.project.habitflow.service.impl;

import com.project.habitflow.entity.Trophy;
import com.project.habitflow.entity.User;
import com.project.habitflow.entity.UserTrophy;
import com.project.habitflow.repository.TrophyRepository;
import com.project.habitflow.repository.UserTrophyRepository;
import com.project.habitflow.service.TrophyService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrophyServiceImpl implements TrophyService {

    private final TrophyRepository trophyRepository;
    private final UserTrophyRepository userTrophyRepository;

    public TrophyServiceImpl(TrophyRepository trophyRepository, UserTrophyRepository userTrophyRepository) {
        this.trophyRepository = trophyRepository;
        this.userTrophyRepository = userTrophyRepository;
    }

    @Override
    public Trophy createTrophy(String name, String description, Trophy.Type type, int requirementValue) {
        Trophy trophy = new Trophy(name, description, type, requirementValue);
        return trophyRepository.save(trophy);
    }

    @Override
    public List<Trophy> getAllTrophies() {
        return trophyRepository.findAll();
    }

    @Override
    public UserTrophy awardTrophy(User user, Trophy trophy) {
        if (userTrophyRepository.existsByUserAndTrophy(user, trophy)) {
            return userTrophyRepository.findByUserAndTrophy(user, trophy);
        }

        UserTrophy userTrophy = new UserTrophy(user, trophy, LocalDate.now());
        return userTrophyRepository.save(userTrophy);
    }

    @Override
    public List<UserTrophy> getUserTrophies(User user) {
        return userTrophyRepository.findAllByUser(user);
    }

    @Override
    public void checkAndAwardTrophies(User user) {
        List<Trophy> allTrophies = trophyRepository.findAll();

        for (Trophy trophy : allTrophies) {
            boolean alreadyHas = userTrophyRepository.existsByUserAndTrophy(user, trophy);
            if (alreadyHas) continue;

            boolean meetsRequirement = switch (trophy.getType()) {
                case STREAK -> user.getLongestStreak() >= trophy.getRequirementValue();
                case TOTAL_LOGS -> user.getTotalHabitLogs() >= trophy.getRequirementValue();
                case HABIT_CREATED -> user.getHabits().size() >= trophy.getRequirementValue();
                case OTHER -> false; // puoi gestire logiche personalizzate
            };

            if (meetsRequirement) {
                awardTrophy(user, trophy);
            }
        }
    }
}
