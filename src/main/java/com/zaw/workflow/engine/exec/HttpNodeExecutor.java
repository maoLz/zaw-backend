package com.zaw.workflow.engine.exec;

import cn.hutool.core.collection.CollectionUtil;
import com.zaw.workflow.enums.ExecutorStatus;
import com.zaw.workflow.entity.FlowInstance;
import com.zaw.workflow.entity.FlowNode;
import com.zaw.workflow.entity.NodeExecutorRecord;
import com.zaw.workflow.engine.config.HttpNodeConfig;
import com.zaw.workflow.dto.FlowExecContext;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * HTTP 执行器（增强版）：
 * 支持：
 * - PATH / QUERY / BODY / HEADER 参数
 * - ${context.xxx} 取 FlowInstance.context
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class HttpNodeExecutor implements NodeExecutor {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String execute(FlowExecContext context) throws Exception {
        FlowNode node = context.getCurrentNode();
        FlowInstance instance = context.getFlowInstance();
        NodeExecutorRecord record = context.getCurrentRecord();

        if (node.getConfig() == null || node.getConfig().isBlank()) {
            throw new IllegalArgumentException("HTTP node config is empty");
        }

        /**
         * step1. 加载node config 配置
         */
        HttpNodeConfig cfg = objectMapper.readValue(node.getConfig(), HttpNodeConfig.class);
        String url = cfg.getUrl();
        String method = cfg.getMethod().toUpperCase();

        // -------- context --------
        JsonNode contextNode = parseContext(instance.getContext());

        // -------- headers --------
        HttpHeaders headers = new HttpHeaders();
        if(CollectionUtil.isNotEmpty(cfg.getHeaders())){
            for (Map.Entry<String, String> e : cfg.getHeaders().entrySet()) {
                String value = resolve(e.getValue(), contextNode);
                headers.add(e.getKey(), value);
            }
        }
        if(!headers.containsKey("Content-Type")){
            headers.setContentType(MediaType.APPLICATION_JSON);
        }

        // -------- params --------
        ObjectNode bodyNode = objectMapper.createObjectNode();
        StringBuilder queryBuilder = new StringBuilder();
        if(CollectionUtil.isNotEmpty(cfg.getParams())){
            for(HttpNodeConfig.HttpParam param : cfg.getParams()){
                String name = param.getName();
                String location = param.getLocation();
                JsonNode raw = param.getValue();
                JsonNode resolvedNode = resolveTemplate(raw, contextNode);
                switch (location) {
                    case "PATH" -> {
                        url = url.replace("{" + name + "}", resolvedNode.asText());
                    }
                    case "QUERY" -> {
                        if (queryBuilder.length() > 0) queryBuilder.append("&");
                        queryBuilder.append(name)
                                .append("=")
                                .append(URLEncoder.encode(resolvedNode.asText(), StandardCharsets.UTF_8));
                    }
                    case "HEADER" -> headers.add(name, resolvedNode.asText());
                    case "BODY" -> {
                        if (resolvedNode.isTextual() && StringUtils.isNotBlank(param.getType())) {
                            bodyNode.putPOJO(name, cast(resolvedNode.asText(), param.getType()));
                        } else {
                            bodyNode.set(name, resolvedNode);
                        }
                    }
                }
            }
        }



        if (queryBuilder.length() > 0) {
            url += (url.contains("?") ? "&" : "?") + queryBuilder;
        }

        // -------- http entity --------
        HttpEntity<?> entity;
        JSONObject input = new JSONObject();
        input.put("url", url);
        input.put("headers", headers);
        input.put("body", bodyNode);
        record.setInput(input.toString());
        if ("GET".equals(method)) {
            entity = new HttpEntity<>(headers);
        } else {
            entity = new HttpEntity<>(bodyNode.toString(), headers);
            log.info("HTTP request body: {}", bodyNode.toString());
        }


        ResponseEntity<String> resp = restTemplate.exchange(
                url,
                HttpMethod.valueOf(method),
                entity,
                String.class
        );

        // -------- response --------
        ObjectNode result = objectMapper.createObjectNode();
        result.put("status", resp.getStatusCode().value());
        String res = resp.getBody();
        JSONObject resultObject = JSONObject.parse(res);
        try {
            record.setOutput(resultObject.toString());
        }catch (Exception e){
            record.setOutput(res);
        }
        try {
            result.set("body", objectMapper.readTree(res));
        } catch (Exception e) {
            result.put("body", resp.getBody());
        }
        if(resultObject.getInteger("code") != 0){
            record.setStatus(ExecutorStatus.FAILED);
            throw new RuntimeException("Http node execute failed: " + resultObject.getString("message"));
        }
        record.setStatus(ExecutorStatus.SUCCESS);
        return result.toString();
    }


    // ================= helper =================

    private JsonNode parseContext(String ctx) {
        try {
            return (ctx == null || ctx.isBlank())
                    ? objectMapper.createObjectNode()
                    : objectMapper.readTree(ctx);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid FlowInstance.context JSON", e);
        }
    }

    /**
     * 支持 ${context.xxx}""
     */
    private String resolve(String value, JsonNode context) {
        return TemplateResolver.resolve(value, context);

    }

    private Object cast(String value, String type) {
        return switch (type) {
            case "NUMBER" -> value.contains(".") ? Double.valueOf(value) : Long.valueOf(value);
            case "BOOLEAN" -> Boolean.valueOf(value);
            case "STRING" -> value;
            default -> value;
        };
    }


    private JsonNode resolveTemplate(JsonNode template, JsonNode context) {
        if (template.isTextual()) {
            String v = template.asText();
            return new TextNode(resolve(v, context));
        }

        if (template.isObject()) {
            ObjectNode obj = objectMapper.createObjectNode();
            template.fields().forEachRemaining(e ->
                    obj.set(e.getKey(), resolveTemplate(e.getValue(), context))
            );
            return obj;
        }

        if (template.isArray()) {
            ArrayNode arr = objectMapper.createArrayNode();
            template.forEach(n -> arr.add(resolveTemplate(n, context)));
            return arr;
        }

        return template;
    }


}
