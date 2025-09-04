package com.project.habitflow.repository;

import com.project.habitflow.entity.User;
import com.project.habitflow.entity.UserScore;
import com.project.habitflow.response.UserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserScoreRepository extends JpaRepository<UserScore, Long> {
    Optional<UserScore> findByUser(User user);
}
