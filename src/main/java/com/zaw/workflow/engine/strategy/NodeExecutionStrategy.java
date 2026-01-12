package com.zaw.workflow.engine.strategy;

import com.zaw.workflow.entity.FlowInstance;
import com.zaw.workflow.entity.FlowNode;
import com.zaw.workflow.enums.FlowOperationType;
import com.zaw.workflow.web.FlowExecRequest;
import com.zaw.workflow.dto.FlowExecContext;

public interface NodeExecutionStrategy {

    FlowOperationType getOperationType();

    FlowExecContext prepareContext(FlowExecRequest request);

    FlowInstance loadInstance(FlowExecContext context);

    FlowNode loadCurrentNode(FlowExecContext context);
}
