package com.zaw.workflow.dto;

import com.zaw.workflow.entity.Flow;
import com.zaw.workflow.entity.FlowEdge;
import com.zaw.workflow.entity.FlowInstance;
import com.zaw.workflow.entity.FlowNode;
import com.zaw.workflow.entity.NodeExecutorRecord;
import com.zaw.workflow.web.FlowExecRequest;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 流程执行上下文
 */
@Getter
@Setter
public class FlowExecContext {

    /**
     * 当前请求
     */
    private FlowExecRequest request;

    /**
     * 流程定义
     */
    private Flow flow;

    /**
     * 流程实例
     */
    private FlowInstance flowInstance;

    /**
     * 所有节点
     */
    private List<FlowNode> nodes;

    /**
     * 所有边
     */
    private List<FlowEdge> edges;

    /**
     * 节点映射
     */
    private Map<Long, FlowNode> nodeMap;

    /**
     * 当前节点
     */
    private FlowNode currentNode;

    /**
     * 当前执行记录
     */
    private NodeExecutorRecord currentRecord;

    /**
     * 执行记录列表
     */
    private List<NodeExecutorRecord> execRecords;
}
