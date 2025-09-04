package com.project.habitflow.repository;

import com.project.habitflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'ROLE_ADMIN'")
    long countAdminUsers();

    List<User> findByEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String email, String firstName, String lastName);

    // Trova l'ultimo utente registrato (per statistiche)
    Optional<User> findTopByOrderByCreatedAtDesc();}
