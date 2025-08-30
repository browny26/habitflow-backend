package com.project.habitflow.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class AiAiService {

    @Value("${spring.ai.huggingface.api-key}")
    private String hfApiKey;

    private final WebClient webClient = WebClient.create("http://host.docker.internal:8000");

    public String generateHabitSuggestion(String userInput) {
        // Esempio semplice: puoi sostituire con chiamata a Hugging Face o OpenAI free
        return "Suggerimento AI per creare un habit basato su: " + userInput;
    }

    public String getAiResponse(String prompt) {
        Mono<String> response = webClient.post()
                .uri("/generate")
                .bodyValue(Map.of("prompt", prompt, "max_length", 100))
                .retrieve()
                .bodyToMono(String.class);

        return response.block();
    }
}
