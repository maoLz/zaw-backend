package com.zaw.workflow.engine.util;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

/**
 * JSON 值提取工具：
 *
 * 支持三种表达式：
 * 1) JSONPath   : $.body.content.access_token
 * 2) JSONPointer: /body/content/access_token
 * 3) 顶层 key   : token
 *
 * 取不到值时返回 null（不中断流程）
 */
public final class JsonExtractor {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonExtractor() {
    }

    public static JsonNode extract(JsonNode root, String expression) {
        if (root == null || expression == null || expression.isBlank()) {
            return null;
        }

        String expr = expression.trim();

        try {
            // ---------- JSON Pointer ----------
            if (expr.startsWith("/")) {
                JsonNode v = root.at(expr);
                return (v == null || v.isMissingNode()) ? null : v;
            }

            // ---------- JSONPath ----------
            if (expr.startsWith("$.")) {
                Object document = Configuration.defaultConfiguration()
                        .jsonProvider()
                        .parse(root.toString());

                Object value = JsonPath.read(document, expr);
                return value == null ? null : MAPPER.valueToTree(value);
            }

            // ---------- fallback: top-level key ----------
            JsonNode v = root.get(expr);
            return v == null ? null : v;

        } catch (PathNotFoundException e) {
            // JSONPath 找不到，直接返回 null
            return null;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Failed to extract json value. expr=" + expression, e
            );
        }
    }
}
