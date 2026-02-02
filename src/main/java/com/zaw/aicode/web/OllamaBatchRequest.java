package com.zaw.aicode.web;

import lombok.Data;

@Data
public class OllamaBatchRequest {
    private String systemPrompt;
    private String model;
    private String startNo;
}
