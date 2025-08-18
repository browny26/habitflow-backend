package com.project.habitflow.repository;

import com.project.habitflow.entity.Trophy;
import com.project.habitflow.entity.User;
import com.project.habitflow.entity.UserTrophy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTrophyRepository extends JpaRepository<UserTrophy, Long> {
    boolean existsByUserAndTrophy(User user, Trophy trophy);
    UserTrophy findByUserAndTrophy(User user, Trophy trophy);
    List<UserTrophy> findAllByUser(User user);
}
