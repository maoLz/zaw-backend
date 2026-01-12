package com.zaw.workflow.engine.exec;

import com.zaw.workflow.enums.ExecutorType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class NodeExecutorRegistry {

    private final Map<ExecutorType, NodeExecutor> map = new EnumMap<>(ExecutorType.class);

    public NodeExecutorRegistry(
            HttpNodeExecutor httpNodeExecutor,
            ScriptNodeExecutor scriptNodeExecutor,
            HumanNodeExecutor humanNodeExecutor,
            MqNodeExecutor mqNodeExecutor
    ) {
        map.put(ExecutorType.HTTP, httpNodeExecutor);
        map.put(ExecutorType.SCRIPT, scriptNodeExecutor);
        map.put(ExecutorType.HUMAN, humanNodeExecutor);
        map.put(ExecutorType.MQ, mqNodeExecutor);
    }

    public NodeExecutor getRequired(ExecutorType type) {
        if (type == null) {
            throw new IllegalArgumentException("executorType is null");
        }
        NodeExecutor ex = map.get(type);
        if (ex == null) {
            throw new IllegalArgumentException("No NodeExecutor registered for executorType=" + type);
        }
        return ex;
    }
}
