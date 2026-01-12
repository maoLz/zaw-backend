package com.zaw.workflow.web;


import com.zaw.workflow.enums.ExecutorType;
import com.zaw.workflow.enums.NodeType;
import lombok.Builder;
import lombok.Data;

/**
 * @author lizhong.zhang@yituishui.com
 * @since 2026/1/9
 */
@Data
@Builder
public class CreateFlowNodeRequest {

    private Long flowId;

    private String nodeKey;

    private String nodeName;

    private NodeType nodeType;

    private ExecutorType executorType;

    @Builder.Default
    private String config = "{}";

    @Builder.Default
    private Integer positionX = 0;

    @Builder.Default
    private Integer positionY = 0;

}
