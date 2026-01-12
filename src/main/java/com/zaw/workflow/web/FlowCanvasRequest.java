package com.zaw.workflow.web;

import lombok.Data;

import java.util.List;

/**
 * 流程画布提交请求
 */
@Data
public class FlowCanvasRequest {

    private Long flowId;

    private List<NodeRq> nodes;

    private List<EdgeRq> edges;

    /**
     * 节点请求
     */
    @Data
    public static class NodeRq {
        private Long id;
        private Integer x;
        private Integer y;
    }

    /**
     * 连线请求
     */
    @Data
    public static class EdgeRq {
        private Long from;
        private Long to;
    }
}
