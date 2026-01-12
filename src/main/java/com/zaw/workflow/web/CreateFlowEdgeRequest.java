package com.zaw.workflow.web;


import lombok.Builder;
import lombok.Data;

/**
 * @author lizhong.zhang@yituishui.com
 * @since 2026/1/9
 */
@Data
@Builder
public class CreateFlowEdgeRequest {

    private Long flowId;
    private Long fromNodeId;
    private Long toNodeId;

}
