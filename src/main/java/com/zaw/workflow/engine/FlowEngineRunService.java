package com.zaw.workflow.engine;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.zaw.workflow.dto.FlowExecContext;
import com.zaw.workflow.engine.strategy.NodeExecutionStrategy;
import com.zaw.workflow.engine.strategy.NodeExecutionStrategyFactory;
import com.zaw.workflow.entity.FlowInstance;
import com.zaw.workflow.entity.FlowNode;
import com.zaw.workflow.enums.FlowOperationType;
import com.zaw.workflow.repository.FlowInstanceRepository;
import com.zaw.workflow.web.FlowExecRequest;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class FlowEngineRunService {

    private final FlowEngineImpl flowEngineImpl;
    private final NodeExecutionStrategyFactory nodeExecutionStrategyFactory;
    private final FlowInstanceRepository flowInstanceRepository;

    public void continueTask(FlowExecRequest request) {
        NodeExecutionStrategy strategy = nodeExecutionStrategyFactory.getStrategy(FlowOperationType.CONTINUE);
        FlowExecContext context = strategy.prepareContext(request);
        context.setRequest(request);
        FlowInstance instance = strategy.loadInstance(context);
        FlowNode current = strategy.loadCurrentNode(context);
        context.setCurrentNode(current);
        instance = flowInstanceRepository.save(instance);
        flowEngineImpl.run(instance.getId(), current, context, strategy);
    }

    public void start(FlowExecRequest request) {
        NodeExecutionStrategy strategy = nodeExecutionStrategyFactory.getStrategy(FlowOperationType.START);
        FlowExecContext context = strategy.prepareContext(request);
        context.setRequest(request);
        FlowInstance instance = strategy.loadInstance(context);
        FlowNode current = strategy.loadCurrentNode(context);
        instance = flowInstanceRepository.save(instance);
        flowEngineImpl.run(instance.getId(), current, context, strategy);
    }

}
