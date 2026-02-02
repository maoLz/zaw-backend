package com.zaw.business.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class FieldInfo {
    public String name;
    public Set<String> types;
    public List<?> enums;
    public boolean required;
    public List<FieldInfo> children = new ArrayList<>();

    public FieldInfo(String name, Set<String> types, boolean required) {
        this.name = name;
        this.types = types;
        this.required = required;
    }
}
