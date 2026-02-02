package com.zaw.business.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.zaw.business.dto.ApiResponse;
import com.zaw.business.dto.FieldInfo;
import com.zaw.business.dto.FieldInfoJsonGenerator;
import com.zaw.business.dto.OpenApiLoader;
import com.zaw.business.dto.RequestSchemaExtractor;
import com.zaw.business.dto.ResponseSchemaExtractor;
import com.zaw.business.dto.SchemaParser;
import com.zaw.common.web.R;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lizhong.zhang@yituishui.com
 * @since 2026/1/1
 */
@RestController
@RequestMapping("/api/")
public class SwaggerApiController {

    public static OpenAPI RebellOpenAPI = null;

    public static OpenAPI FlowOpenApi = null;

    private static Map<String, String> openAPIUrlMap = new HashMap<>();

    private static Map<String,OpenAPI> openAPIMap = new HashMap<>();
    static {
        openAPIUrlMap.put("flow", "http://127.0.0.1:7001/v3/api-docs");
        openAPIUrlMap.put("rebell", "http://127.0.0.1:8080/v3/api-docs");
    }


    @GetMapping("/info/{apiTag}")
    public R<String> info(@PathVariable String apiTag, @RequestParam String path){
        path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        OpenAPI openAPI = openAPIMap.get(apiTag);
        if(openAPI == null){
            String url = openAPIUrlMap.get(apiTag);
            openAPI = OpenApiLoader.load(url);
            openAPIMap.put(apiTag,openAPI);
        }
        PathItem pathItem = openAPI.getPaths().get(path);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setUrl(path);
        Map<PathItem.HttpMethod, Operation> operationMap = pathItem.readOperationsMap();
        for (Map.Entry<PathItem.HttpMethod, Operation> entry : operationMap.entrySet()) {
            PathItem.HttpMethod method = entry.getKey(); // 这里就是 GET, POST, PUT 等枚举
            Operation operation = entry.getValue();
            System.out.println("检测到方法: " + method.name());
            apiResponse.setOperation(operation);
            // 根据方法类型做不同处理
            if (method == PathItem.HttpMethod.POST) {
                Schema<?> requestSchema =
                        RequestSchemaExtractor.extract(operation.getRequestBody());
                List<FieldInfo> requestFields =
                        SchemaParser.parseSchema(openAPI, requestSchema);
                apiResponse.addSchema(requestSchema.get$ref(), FieldInfoJsonGenerator.generate(requestFields));
                Schema<?> responseSchema =
                        ResponseSchemaExtractor.extract(operation.getResponses());
                List<FieldInfo> responseFields =
                        SchemaParser.parseSchema(openAPI, responseSchema);
                apiResponse.addSchema(responseSchema.get$ref(), FieldInfoJsonGenerator.generate(responseFields));
                // 处理 POST 特有逻辑
            }
            else if (method == PathItem.HttpMethod.GET) {
                // 处理 GET 特有逻辑
                Schema<?> responseSchema =
                        ResponseSchemaExtractor.extract(operation.getResponses());
                List<FieldInfo> responseFields =
                        SchemaParser.parseSchema(openAPI, responseSchema);
                apiResponse.addSchema(responseSchema.get$ref(), FieldInfoJsonGenerator.generate(responseFields));
            }
        }


        return R.ok(JSON.toJSONString(apiResponse));
    }


}
