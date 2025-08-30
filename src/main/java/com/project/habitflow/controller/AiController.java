package com.project.habitflow.controller;

import com.project.habitflow.service.AiAiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiAiService aiAiService;

    public AiController(AiAiService aiAiService) {
        this.aiAiService = aiAiService;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String prompt) {
        return aiAiService.getAiResponse(prompt);
    }
}
