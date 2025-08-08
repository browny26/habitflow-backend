package com.project.habitflow.repository;

import com.project.habitflow.entity.AiFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiFeedbackRepository extends JpaRepository<AiFeedback, Long> {
}
