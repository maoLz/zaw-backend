package com.zaw.workflow.mapper;


import com.zaw.workflow.entity.FlowNode;
import com.zaw.workflow.web.CreateFlowNodeRequest;

/**
 * @author lizhong.zhang@yituishui.com
 * @since 2026/1/9
 */
public class FlowNodeMapper {

    public static FlowNode toEntity(CreateFlowNodeRequest request) {
        FlowNode node = new FlowNode();
        node.setNodeKey(request.getNodeKey());
        node.setNodeName(request.getNodeName());
        node.setNodeType(request.getNodeType());
        node.setExecutorType(request.getExecutorType());
        node.setConfig(request.getConfig());
        node.setPositionX(request.getPositionX());
        node.setPositionY(request.getPositionY());
        return node;
    }

}
