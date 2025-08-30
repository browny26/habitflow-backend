package com.project.habitflow.service;

import com.project.habitflow.entity.AiFeedback;
import com.project.habitflow.entity.Habit;
import com.project.habitflow.entity.User;
import com.project.habitflow.repository.AiFeedbackRepository;
import com.project.habitflow.repository.HabitRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AiFeedbackServiceImpl implements AiFeedbackService {

    private final AiFeedbackRepository aiFeedbackRepository;
    private final HabitRepository habitRepository;
    private final AiAiService aiAiService;

    public AiFeedbackServiceImpl(AiFeedbackRepository aiFeedbackRepository,
                                 HabitRepository habitRepository,
                                 AiAiService aiAiService) {
        this.aiFeedbackRepository = aiFeedbackRepository;
        this.habitRepository = habitRepository;
        this.aiAiService = aiAiService;
    }

    @Override
    public AiFeedback sendUserInput(User user, String input) {
        // Chiamata all'AI per generare suggerimento completo
        String aiResponse = aiAiService.generateHabitSuggestion(input);

        // Salva l’AI feedback
        AiFeedback feedback = new AiFeedback();
        feedback.setUser(user);
        feedback.setUserInput(input);
        feedback.setAiResponse(aiResponse);
        feedback.setTimestamp(LocalDateTime.now());
        aiFeedbackRepository.save(feedback);

        // Estrai nome breve dall'AI response
        String habitName;
        if (aiResponse.length() > 50) {
            int endIndex = aiResponse.indexOf('.', 40); // cerca un punto dopo i primi 40 caratteri
            habitName = endIndex != -1 ? aiResponse.substring(0, endIndex + 1) : aiResponse.substring(0, 50);
        } else {
            habitName = aiResponse;
        }

        // Crea il nuovo Habit
        Habit habit = new Habit();
        habit.setName(habitName.trim());
        habit.setDescription(aiResponse.trim());
        habit.setUser(user);
        habitRepository.save(habit);

        return feedback;
    }

    @Override
    public List<AiFeedback> getAllFeedbacksForUser(User user) {
        return aiFeedbackRepository.findAll().stream()
                .filter(f -> f.getUser().equals(user))
                .toList();
    }

    @Override
    public List<AiFeedback> getChatHistory(User user) {
        return aiFeedbackRepository.findAllByUserOrderByTimestampAsc(user);
    }
}
