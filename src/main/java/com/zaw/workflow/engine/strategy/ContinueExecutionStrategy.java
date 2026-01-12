package com.zaw.workflow.engine.strategy;


import com.zaw.workflow.enums.ExecutorStatus;
import com.zaw.workflow.entity.Flow;
import com.zaw.workflow.entity.FlowInstance;
import com.zaw.workflow.entity.FlowNode;
import com.zaw.workflow.enums.FlowOperationType;
import com.zaw.workflow.entity.NodeExecutorRecord;
import com.zaw.workflow.dto.FlowExecContext;
import com.zaw.workflow.repository.FlowEdgeRepository;
import com.zaw.workflow.repository.FlowInstanceRepository;
import com.zaw.workflow.repository.FlowNodeRepository;
import com.zaw.workflow.repository.FlowRepository;
import com.zaw.workflow.repository.NodeExecutorRecordRepository;
import com.zaw.workflow.web.FlowExecRequest;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lizhong.zhang@yituishui.com
 * @since 2026/1/3
 */
@Component
@AllArgsConstructor
public class ContinueExecutionStrategy implements NodeExecutionStrategy{

    private final FlowInstanceRepository flowInstanceRepository;

    private final FlowRepository flowRepository;

    private final FlowNodeRepository flowNodeRepository;

    private final FlowEdgeRepository flowEdgeRepository;

    private final NodeExecutorRecordRepository nodeExecutorRecordRepository;

    @Override
    public FlowOperationType getOperationType() {
        return FlowOperationType.CONTINUE;
    }

    @Override
    public FlowExecContext prepareContext(FlowExecRequest request) {
        FlowExecContext context = new FlowExecContext();
        FlowInstance flowInstance = flowInstanceRepository.findById(request.getInstanceId())
                .orElseThrow(()-> new IllegalArgumentException("FlowInstance not found with id: " + request.getInstanceId()));
        context.setFlowInstance(flowInstance);
        Long flowId = flowInstance.getFlowId();
        Flow flow = flowRepository.findById(flowId)
                .orElseThrow(()-> new IllegalArgumentException("Flow not found with id: " + flowId));
        context.setFlow(flow);
        context.setNodes(flowNodeRepository.findByFlowId(flowId));
        context.setEdges(flowEdgeRepository.findByFlowId(flowId));
        context.setNodeMap(context.getNodes().stream()
                .collect(Collectors.toMap(FlowNode::getId, Function.identity())));
        context.setExecRecords(nodeExecutorRecordRepository.findByFlowInstanceIdOrderByIdAsc(request.getInstanceId()));
        NodeExecutorRecord waitingNode = context.getExecRecords().stream()
                .filter(record -> record.getStatus() == ExecutorStatus.WAITING || record.getStatus() == ExecutorStatus.FAILED)
                .findFirst()
                .orElseThrow(()-> new IllegalArgumentException("No waiting node found"));
        context.setCurrentRecord(waitingNode);
        return  context;
    }

    @Override
    public FlowInstance loadInstance(FlowExecContext context) {
        return context.getFlowInstance();
    }

    @Override
    public FlowNode loadCurrentNode(FlowExecContext context) {

        FlowNode node = flowNodeRepository.findById(context.getCurrentRecord().getNodeId()).orElseThrow(()->
                new IllegalArgumentException("FlowNode not found with id: " + context.getCurrentRecord().getNodeId()));
        context.setCurrentNode(node);
        return node;
    }


}
