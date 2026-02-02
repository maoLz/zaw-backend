package com.zaw.aicode.web;

import lombok.Data;

@Data
public class OllamaChatRequest {
    private String systemPrompt;
    private String userQuestion;
    private String model;
}
