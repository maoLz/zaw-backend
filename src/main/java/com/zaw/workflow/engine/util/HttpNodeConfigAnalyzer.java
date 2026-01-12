package com.zaw.workflow.engine.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTTP Node 配置分析器（增量模式）：
 * - 支持单个 configJson 分析
 * - 结果累积到传入的 Result 中
 */
public final class HttpNodeConfigAnalyzer {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** ${context.xxx} */
    private static final Pattern CONTEXT_PATTERN =
            Pattern.compile("\\$\\{\\s*context\\.([a-zA-Z0-9_\\.]+)\\s*}");

    private HttpNodeConfigAnalyzer() {
    }

    /**
     * 分析单个 HTTP Node 配置，并把结果累积到 result 中
     */
    public static void analyze(
            String nodeKey,
            String configJson,
            Result result
    ) {
        if (configJson == null || configJson.isBlank() || result == null) {
            return;
        }

        try {
            JsonNode root = MAPPER.readTree(configJson);

            // 1️⃣ 扫描 context 变量
            scanNode(root, result.contextVars);

            // 2️⃣ 扫描 outputMapping
            if (root.has("outputMapping") && root.get("outputMapping").isObject()) {
                root.get("outputMapping").fields().forEachRemaining(e ->
                        result.outputVars.add(
                                new OutputVar(
                                        nodeKey,
                                        e.getKey(),
                                        e.getValue().asText()
                                )
                        )
                );
            }

        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Invalid http node config, nodeKey=" + nodeKey, e
            );
        }
    }

    // ================= 内部扫描 =================

    private static void scanNode(JsonNode node, Set<String> contextVars) {
        if (node == null) return;

        if (node.isTextual()) {
            extractContextVars(node.asText(), contextVars);
            return;
        }

        if (node.isObject()) {
            node.fields().forEachRemaining(e -> scanNode(e.getValue(), contextVars));
            return;
        }

        if (node.isArray()) {
            for (JsonNode n : node) {
                scanNode(n, contextVars);
            }
        }
    }

    private static void extractContextVars(String text, Set<String> vars) {
        Matcher m = CONTEXT_PATTERN.matcher(text);
        while (m.find()) {
            vars.add(m.group(1));
        }
    }

    // ================= Result（累积容器） =================

    public static final class Result {

        /** 所有用到的 context 变量（自动去重） */
        private final Set<String> contextVars = new LinkedHashSet<>();

        /** 所有 outputMapping（按节点记录） */
        private final List<OutputVar> outputVars = new ArrayList<>();

        public List<String> getContext() {
            return new ArrayList<>(contextVars);
        }

        public List<OutputVar> getOutput() {
            return outputVars;
        }

        public ObjectNode toJson() {
            ObjectNode root = MAPPER.createObjectNode();
            root.putPOJO("context", getContext());

            ArrayNode out = MAPPER.createArrayNode();
            for (OutputVar v : outputVars) {
                ObjectNode o = MAPPER.createObjectNode();
                o.put("nodeKey", v.nodeKey);
                o.put("key", v.key);
                o.put("expr", v.expr);
                out.add(o);
            }
            root.set("output", out);
            return root;
        }
    }

    public static final class OutputVar {
        private final String nodeKey;
        private final String key;
        private final String expr;

        public OutputVar(String nodeKey, String key, String expr) {
            this.nodeKey = nodeKey;
            this.key = key;
            this.expr = expr;
        }

        public String getNodeKey() {
            return nodeKey;
        }

        public String getKey() {
            return key;
        }

        public String getExpr() {
            return expr;
        }
    }
}
