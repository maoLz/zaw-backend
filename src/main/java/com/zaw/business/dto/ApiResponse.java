package com.zaw.business.dto;


import io.swagger.v3.oas.models.Operation;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lizhong.zhang@yituishui.com
 * @since 2026/1/1
 */
@Data
public class ApiResponse {

    private String url;


    private Operation operation;

    private Map<String,Object> schemas = new HashMap<>();

    public void addSchema(String name, Object schema) {
        schemas.put(name, schema);
    }


}
