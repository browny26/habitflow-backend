package com.project.habitflow.service;

import com.project.habitflow.entity.Trophy;
import com.project.habitflow.entity.User;
import com.project.habitflow.entity.UserTrophy;

import java.util.List;

public interface TrophyService {
    Trophy createTrophy(String name, String description, Trophy.Type type, int requirementValue);
    List<Trophy> getAllTrophies();
    UserTrophy awardTrophy(User user, Trophy trophy);
    List<UserTrophy> getUserTrophies(User user);
    void checkAndAwardTrophies(User user); // 🔑 controlla se l’utente soddisfa nuove condizioni
}
