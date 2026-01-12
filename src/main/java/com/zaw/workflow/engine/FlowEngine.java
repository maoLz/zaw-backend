package com.zaw.workflow.engine;

import com.zaw.workflow.entity.FlowInstance;
import com.zaw.workflow.web.FlowExecRequest;

public interface FlowEngine {


    FlowInstance start(FlowExecRequest request);


    FlowInstance continueTask(FlowExecRequest request);
}
