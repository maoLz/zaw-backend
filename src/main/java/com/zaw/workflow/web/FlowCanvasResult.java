package com.zaw.workflow.web;

import com.zaw.workflow.entity.FlowEdge;
import com.zaw.workflow.entity.FlowNode;
import lombok.Data;

import java.util.List;

/**
 * 流程画布结果
 */
@Data
public class FlowCanvasResult {

    private List<FlowNode> nodes;

    private List<FlowEdge> edges;
}
