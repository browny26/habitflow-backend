package com.project.habitflow.repository;

import com.project.habitflow.entity.UserTrophy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTrophyRepository extends JpaRepository<UserTrophy, Long> {
}
