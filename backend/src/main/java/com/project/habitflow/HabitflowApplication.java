package com.project.habitflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@EnableJpaRepositories(basePackages = "com.project.habitflow.repository")
//@EntityScan(basePackages = "com.project.habitflow.entity")
@SpringBootApplication
public class HabitflowApplication {

	public static void main(String[] args) {
		SpringApplication.run(HabitflowApplication.class, args);
	}

}
