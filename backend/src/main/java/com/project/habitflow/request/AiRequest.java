package com.project.habitflow.request;

public class AiRequest {
    private String user_input;
    private String difficulty;

    // costruttori, getter e setter
    public AiRequest() {}
    public AiRequest(String user_input, String difficulty) {
        this.user_input = user_input;
        this.difficulty = difficulty;
    }
    public String getUser_input() { return user_input; }
    public void setUser_input(String user_input) { this.user_input = user_input; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
}

