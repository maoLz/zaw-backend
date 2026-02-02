package com.zaw.business.dto;

import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;

import java.util.Map;

public class RequestSchemaExtractor {

    public static Schema<?> extract(RequestBody requestBody) {
        if (requestBody == null || requestBody.getContent() == null) {
            return null;
        }

        // 优先 application/json，其次随便取一个
        MediaType mediaType = getPreferredMediaType(requestBody.getContent());
        if (mediaType == null) {
            return null;
        }

        return mediaType.getSchema();
    }

    private static MediaType getPreferredMediaType(Map<String, MediaType> content) {
        if (content.containsKey("application/json")) {
            return content.get("application/json");
        }
        return content.values().stream().findFirst().orElse(null);
    }
}
