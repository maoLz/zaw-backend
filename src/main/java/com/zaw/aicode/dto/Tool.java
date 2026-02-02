package com.zaw.aicode.dto;

import lombok.Data;

import java.util.Map;
@Data
public class Tool {

    private String type; // function
    private Function function;

    public static class Function {
        private String name;
        private String description;
        private Map<String, Object> parameters;
        private Boolean strict;
    }
}
