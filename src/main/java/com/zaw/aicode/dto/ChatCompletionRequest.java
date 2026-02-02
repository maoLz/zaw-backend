package com.zaw.aicode.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatCompletionRequest {

    private String model;                 // deepseek-chat | deepseek-reasoner
    private List<Message> messages;        // 对话消息
    private Thinking thinking;             // 思考模式
    private Double temperature;
    private Double topP;
    private Integer maxTokens;
    private Double frequencyPenalty;
    private Double presencePenalty;
    private ResponseFormat responseFormat;
    private Object stop;                   // String 或 List<String>
    private Boolean stream;
    private StreamOptions streamOptions;
    private List<Tool> tools;
    private Object toolChoice;              // none | auto | required | 指定 tool
    private Boolean logprobs;
    private Integer topLogprobs;

    // getters / setters
}
