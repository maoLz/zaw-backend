package com.zaw.workflow.engine.strategy;

import com.zaw.workflow.entity.Flow;
import com.zaw.workflow.entity.FlowInstance;
import com.zaw.workflow.enums.FlowInstanceStatus;
import com.zaw.workflow.entity.FlowNode;
import com.zaw.workflow.enums.FlowOperationType;
import com.zaw.workflow.enums.NodeType;
import com.zaw.workflow.dto.FlowExecContext;
import com.zaw.workflow.repository.FlowEdgeRepository;
import com.zaw.workflow.repository.FlowInstanceRepository;
import com.zaw.workflow.repository.FlowNodeRepository;
import com.zaw.workflow.repository.FlowRepository;
import com.zaw.workflow.web.FlowExecRequest;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class StartExecutionStrategy implements NodeExecutionStrategy {

    private final FlowRepository flowRepository;

    private final FlowInstanceRepository flowInstanceRepository;

    private final FlowNodeRepository flowNodeRepository;

    private final FlowEdgeRepository flowEdgeRepository;


    @Override
    public FlowOperationType getOperationType() {
        return FlowOperationType.START;
    }

    @Override
    public FlowExecContext prepareContext(FlowExecRequest request) {
        Flow flow = flowRepository.findById(request.getFlowId())
                .orElseThrow(() -> new IllegalArgumentException("Flow not found: " + request.getFlowId()));
        FlowExecContext context = new FlowExecContext();
        context.setFlow(flow);
        Long flowId = flow.getId();
        context.setNodes(flowNodeRepository.findByFlowId(flowId));
        context.setEdges(flowEdgeRepository.findByFlowId(flowId));
        context.setNodeMap(context.getNodes().stream()
                .collect(Collectors.toMap(FlowNode::getId, Function.identity())));
        return context;
    }

    @Override
    public FlowInstance loadInstance(FlowExecContext context) {
        Flow flow = context.getFlow();
        FlowExecRequest request = context.getRequest();
        FlowInstance instance = new FlowInstance();
        instance.setFlowId(flow.getId());
        instance.setFlowVersion(flow.getFlowVersion());
        instance.setBusinessKey(request.getBusinessKey());
        instance.setStatus(FlowInstanceStatus.RUNNING);
        instance.setStartTime(new Date());
        instance.setContext(request.getContextJson());
        instance.setInput(request.getContextJson());
        instance = flowInstanceRepository.save(instance);
        context.setFlowInstance(instance);
        return instance;
    }

    @Override
    public FlowNode loadCurrentNode(FlowExecContext context) {
        FlowNode current = context.getNodes().stream()
                .filter(n -> n.getNodeType() == NodeType.START)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("START node not found"));
        context.setCurrentNode(current);
        return current;
    }

}
