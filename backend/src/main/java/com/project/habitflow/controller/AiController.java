package com.project.habitflow.controller;

import com.project.habitflow.request.AiRequest;
import com.project.habitflow.service.AiAiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiAiService aiAiService;

    public AiController(AiAiService aiAiService) {
        this.aiAiService = aiAiService;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody AiRequest aiRequest) {
        return aiAiService.getAiResponse(aiRequest);
    }
}
