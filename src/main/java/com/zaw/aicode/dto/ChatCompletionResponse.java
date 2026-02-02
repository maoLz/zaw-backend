package com.zaw.aicode.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatCompletionResponse {

    private String id;
    private String object;          // chat.completion
    private Long created;
    private String model;
    private String systemFingerprint;
    private List<Choice> choices;
    private Usage usage;

    // getters / setters
}
