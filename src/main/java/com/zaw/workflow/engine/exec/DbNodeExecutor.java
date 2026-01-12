package com.zaw.workflow.engine.exec;

import com.zaw.workflow.entity.FlowInstance;
import com.zaw.workflow.entity.FlowNode;
import com.zaw.workflow.dto.FlowExecContext;
import com.zaw.workflow.engine.DataSourceRegistry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DB 节点执行器
 */
@Component
@RequiredArgsConstructor
public class DbNodeExecutor implements NodeExecutor {

    private final DataSourceRegistry dataSourceRegistry;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String execute(FlowExecContext context) throws Exception {

        FlowNode node = context.getCurrentNode();
        FlowInstance instance = context.getFlowInstance();
        if (node.getConfig() == null || node.getConfig().isBlank()) {
            throw new IllegalArgumentException("DB node config is empty");
        }

        JsonNode cfg = objectMapper.readTree(node.getConfig());

        String database = text(cfg, "database");
        String sql = text(cfg, "sql");
        String resultType = cfg.has("resultType")
                ? cfg.get("resultType").asText()
                : "LIST";

        NamedParameterJdbcTemplate jdbc =
                dataSourceRegistry.get(database);

        Map<String, Object> params =
                resolveParams(cfg.get("params"), instance);

        Object result;

        switch (resultType) {
            case "ONE" -> {
                Map<String, Object> row =
                        jdbc.queryForMap(sql, params);
                result = row;
            }
            case "AFFECTED_ROWS" -> {
                int count =
                        jdbc.update(sql, params);
                result = Map.of("affectedRows", count);
            }
            case "LIST" -> {
                List<Map<String, Object>> rows =
                        jdbc.queryForList(sql, params);
                result = rows;
            }
            default -> throw new IllegalArgumentException(
                    "Unsupported resultType: " + resultType
            );
        }

        return objectMapper.writeValueAsString(result);
    }



    // ================= helper =================

    private Map<String, Object> resolveParams(JsonNode paramsNode, FlowInstance instance) {
        Map<String, Object> params = new HashMap<>();
        if (paramsNode == null || !paramsNode.isArray()) {
            return params;
        }

        JsonNode context = parseContext(instance.getContext());

        for (JsonNode p : paramsNode) {
            String name = text(p, "name");
            String rawValue = text(p, "value");
            Object value = resolveValue(rawValue, context);
            params.put(name, value);
        }
        return params;
    }

    private Object resolveValue(String raw, JsonNode context) {
        if (!raw.contains("${")) return raw;
        String key = raw.substring(raw.indexOf("${") + 2, raw.indexOf("}"));
        if (!key.startsWith("context.")) return raw;
        JsonNode v = context.get(key.substring("context.".length()));
        return v == null ? null : v.isNumber() ? v.numberValue() : v.asText();
    }

    private JsonNode parseContext(String ctx) {
        try {
            return ctx == null || ctx.isBlank()
                    ? objectMapper.createObjectNode()
                    : objectMapper.readTree(ctx);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid FlowInstance.context JSON", e);
        }
    }

    private String text(JsonNode cfg, String key) {
        if (!cfg.hasNonNull(key) || cfg.get(key).asText().isBlank()) {
            throw new IllegalArgumentException("Missing config field: " + key);
        }
        return cfg.get(key).asText();
    }
}
