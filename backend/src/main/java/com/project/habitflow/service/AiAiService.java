package com.project.habitflow.service;

import com.project.habitflow.request.AiRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class AiAiService {

    //private final WebClient webClient = WebClient.create("http://host.docker.internal:8000");
    //private final WebClient webClient = WebClient.create("http://ai-service:8000");

    public String generateHabitSuggestion(String userInput) {
        return "Suggerimento AI per creare un habit basato su: " + userInput;
    }

    public String getAiResponse(AiRequest aiRequest) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AiRequest> request = new HttpEntity<>(aiRequest, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://ai-service:8000/api/ai/generate-habit",
                request,
                String.class
        );

        return response.getBody();
    }
}
