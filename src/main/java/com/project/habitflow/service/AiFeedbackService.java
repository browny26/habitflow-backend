package com.project.habitflow.service;

import com.project.habitflow.entity.AiFeedback;
import com.project.habitflow.entity.User;

import java.util.List;

public interface AiFeedbackService {
    AiFeedback sendUserInput(User user, String input);
    List<AiFeedback> getAllFeedbacksForUser(User user);
    List<AiFeedback> getChatHistory(User user);
}
