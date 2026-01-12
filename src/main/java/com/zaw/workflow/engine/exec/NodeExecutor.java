package com.zaw.workflow.engine.exec;


import com.zaw.workflow.dto.FlowExecContext;


/**
 * 节点执行器：负责执行 TASK 节点。
 * 返回 output（建议JSON字符串），FlowEngine 会写入 node_executor_record.output。
 */
public interface NodeExecutor {

    String execute(FlowExecContext context) throws Exception;
}
