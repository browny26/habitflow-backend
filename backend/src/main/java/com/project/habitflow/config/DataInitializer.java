package com.project.habitflow.config;

import com.project.habitflow.entity.User;
import com.project.habitflow.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Controlla se l'admin esiste già
            if (userRepository.findByEmail("admin@habitflow.com").isEmpty()) {
                User admin = new User();
                admin.setFirstName("Admin");
                admin.setLastName("HabitFlow");
                admin.setEmail("admin@habitflow.com");
                admin.setPassword(passwordEncoder.encode("admin123")); // password criptata
                admin.setRole(User.Role.ADMIN);

                userRepository.save(admin);

                System.out.println("Admin utente creato: admin@habitflow.com / admin123");
            }
        };
    }
}
