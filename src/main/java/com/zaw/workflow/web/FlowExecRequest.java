package com.zaw.workflow.web;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 流程执行请求参数
 */
@Getter
@Setter
public class FlowExecRequest {

    /**
     * 流程ID
     */
    private Long flowId;

    /**
     * 流程实例ID（继续执行时使用）
     */
    private Long instanceId;

    /**
     * 业务标识
     */
    private String businessKey;

    /**
     * 上下文JSON
     */
    private String contextJson;

    /**
     * 人工节点提交参数
     */
    private Map<String, Object> humanParam;
}
