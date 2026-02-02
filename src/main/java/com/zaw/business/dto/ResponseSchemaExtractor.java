package com.zaw.business.dto;

import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;

import java.util.Map;

public class ResponseSchemaExtractor {

    public static Schema<?> extract(Map<String, ApiResponse> responses) {
        if (responses == null) {
            return null;
        }

        // 优先 200，其次 2xx，其次第一个
        ApiResponse response = responses.get("200");
        if (response == null) {
            response = responses.entrySet().stream()
                    .filter(e -> e.getKey().startsWith("2"))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(null);
        }

        if (response == null || response.getContent() == null) {
            return null;
        }

        MediaType mediaType = getPreferredMediaType(response.getContent());
        return mediaType != null ? mediaType.getSchema() : null;
    }

    private static MediaType getPreferredMediaType(Map<String, MediaType> content) {
        if (content.containsKey("application/json")) {
            return content.get("application/json");
        }
        return content.values().stream().findFirst().orElse(null);
    }
}
