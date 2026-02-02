package com.zaw.aicode.dto;

import lombok.Data;

@Data
public class Message {

    private String role;      // system | user | assistant | tool
    private String content;

    // assistant 专用（Beta）
    private Boolean prefix;
    private String reasoningContent;

    // tool 专用
    private String toolCallId;

    // getters / setters
}
