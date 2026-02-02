package com.zaw.business.dto;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;

public class OpenApiLoader {

    public static OpenAPI load(String apiDocUrl) {
        return new OpenAPIV3Parser().read(apiDocUrl);
    }
}
