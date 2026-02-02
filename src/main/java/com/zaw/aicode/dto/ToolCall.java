package com.zaw.aicode.dto;

import lombok.Data;

import java.util.List;
@Data
public class ToolCall {

    private String id;
    private String type; // function
    private FunctionCall function;

    public static class FunctionCall {
        private String name;
        private String arguments; // JSON string
    }
}
