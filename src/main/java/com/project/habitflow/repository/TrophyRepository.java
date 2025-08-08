package com.project.habitflow.repository;

import com.project.habitflow.entity.Trophy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrophyRepository extends JpaRepository<Trophy, Long> {
}
