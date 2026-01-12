package com.zaw.workflow.mapper;


import com.zaw.workflow.entity.FlowEdge;
import com.zaw.workflow.web.CreateFlowEdgeRequest;

/**
 * @author lizhong.zhang@yituishui.com
 * @since 2026/1/9
 */
public class FlowEdgeMapper {


    public static FlowEdge toEntity(CreateFlowEdgeRequest request) {
        FlowEdge flowEdge = new FlowEdge();
        flowEdge.setFlowId(request.getFlowId());
        flowEdge.setFromNodeId(request.getFromNodeId());
        flowEdge.setToNodeId(request.getToNodeId());
        return flowEdge;
    }

}
