package com.project.habitflow.repository;

import com.project.habitflow.entity.AiFeedback;
import com.project.habitflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiFeedbackRepository extends JpaRepository<AiFeedback, Long> {
    List<AiFeedback> findAllByUserOrderByTimestampAsc(User user);
}
