package com.zaw.workflow.engine.strategy;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import com.zaw.workflow.enums.FlowOperationType;

import java.util.List;

/**
 * @author lizhong.zhang@yituishui.com
 * @since 2026/1/3
 */
@Component
@AllArgsConstructor
public class NodeExecutionStrategyFactory {

    private final List<NodeExecutionStrategy> strategies;

    public NodeExecutionStrategy getStrategy(FlowOperationType operationType) {
        return strategies.stream()
                .filter(s -> s.getOperationType() == operationType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No strategy found for operationType=" + operationType));
    }


}
