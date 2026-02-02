package com.zaw.workflow.mapper;


import com.zaw.workflow.entity.Flow;
import com.zaw.workflow.enums.FlowStatus;
import com.zaw.workflow.web.CreateFlowRequest;
import com.zaw.workflow.web.UpdateFlowRequest;

/**
 * @author lizhong.zhang@yituishui.com
 * @since 2026/1/9
 */
public class FlowMapper {

    public static Flow toEntity(CreateFlowRequest request) {
        Flow flow = new Flow();
        flow.setName(request.getName());
        flow.setCode(request.getCode());
        flow.setDescription(request.getDescription());
        flow.setContextConfig(request.getContextConfig());

        // 系统强制规则
        flow.setStatus(FlowStatus.DRAFT);
        flow.setFlowVersion(1);
        return flow;
    }

    public static void updateEntity(Flow flow, UpdateFlowRequest request) {
        flow.setName(request.getName());
        flow.setCode(request.getCode());
        flow.setDescription(request.getDescription());
        flow.setContextConfig(request.getContextConfig());
        flow.setFlowVersion(request.getFlowVersion());
        flow.setStatus(request.getStatus());
    }


}
