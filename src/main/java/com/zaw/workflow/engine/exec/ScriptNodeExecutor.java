package com.zaw.workflow.engine.exec;

import com.zaw.workflow.entity.FlowInstance;
import com.zaw.workflow.entity.FlowNode;
import com.zaw.workflow.dto.FlowExecContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * SCRIPT 执行器（最简版）：使用 JS 引擎（若运行环境不带 Nashorn，则需要引入 GraalJS 或改为其他脚本方案）。
 * node.config 示例：
 * {"lang":"js","script":"function run(ctx){ return JSON.stringify({ok:true, ctx:ctx}); } run(context);"}
 */
@Component
public class ScriptNodeExecutor implements NodeExecutor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String execute(FlowExecContext context) throws Exception {
        FlowNode node = context.getCurrentNode();
        FlowInstance instance = context.getFlowInstance();

        if (node.getConfig() == null || node.getConfig().isBlank()) {
            throw new IllegalArgumentException("SCRIPT node config is empty");
        }

        JsonNode cfg = objectMapper.readTree(node.getConfig());
        String lang = cfg.hasNonNull("lang") ? cfg.get("lang").asText() : "js";
        String script = cfg.hasNonNull("script") ? cfg.get("script").asText() : null;
        if (script == null || script.isBlank()) {
            throw new IllegalArgumentException("Missing config field: script");
        }

        // 最简：把 instance.context 传给脚本
        String contextJson = instance.getContext() == null ? "{}" : instance.getContext();

        if (!"js".equalsIgnoreCase(lang) && !"javascript".equalsIgnoreCase(lang)) {
            throw new IllegalArgumentException("Unsupported script lang: " + lang);
        }

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        if (engine == null) {
            throw new IllegalStateException("JavaScript ScriptEngine not found. Consider adding GraalJS or enabling Nashorn.");
        }

        engine.put("context", contextJson);
        Object out = engine.eval(script);
        return out == null ? "" : String.valueOf(out);
    }

}
