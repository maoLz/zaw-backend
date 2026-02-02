package com.zaw.workflow.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zaw.common.web.R;
import com.zaw.workflow.entity.FlowInstance;
import com.zaw.workflow.service.FlowInstanceService;
import com.zaw.workflow.service.FlowService;
import com.zaw.workflow.service.NodeExecutorRecordService;
import com.zaw.workflow.util.FlowInstanceFormatUtils;
import com.zaw.workflow.web.FlowDetailResponse;
import com.zaw.workflow.web.FlowInstanceDetailResponse;
import com.zaw.workflow.web.FlowInstanceResponse;
import com.zaw.workflow.web.NodeExecutorRecordDetailResponse;
import com.zaw.workflow.web.NodeExecutorRecordSimpleResponse;

import java.util.List;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {

    private final FlowInstanceService flowInstanceService;
    private final FlowService flowService;
    private final NodeExecutorRecordService nodeExecutorRecordService;

    @GetMapping("/instance/{flowId}")
    public R<List<FlowInstanceResponse>> instance(@PathVariable Long flowId){
        List<FlowInstance> instances = flowInstanceService.listByFlowId(flowId);
        if (instances.isEmpty()) {
            return R.fail("未找到该流程的实例");
        }

        List<FlowInstanceResponse> responses = instances.stream()
            .map(instance -> {
                FlowInstanceResponse response = new FlowInstanceResponse();
                response.setId(instance.getId());
                response.setBusinessKey(instance.getBusinessKey());
                response.setStatus(instance.getStatus());
                response.setDuration(FlowInstanceFormatUtils.formatDuration(instance.getStartTime(), instance.getEndTime()));
                response.setStartDateTime(FlowInstanceFormatUtils.formatDateTime(
                        FlowInstanceFormatUtils.firstNonNullDate(instance.getStartTime(), instance.getCreateDate())));
                response.setRunNodeNumber(instance.getRunNodeNumber() == null
                        ? null
                        : instance.getRunNodeNumber().longValue());
                response.setErrorMessage(instance.getErrorMessage());
                return response;
            })
            .toList();

        return R.ok(responses);
    }

    @GetMapping("/flow/{id}")
    public R<FlowDetailResponse> detail(@PathVariable Long id) {
        var flow = flowService.detail(id);
        FlowDetailResponse response = new FlowDetailResponse();
        response.setName(flow.getName());
        response.setCode(flow.getCode());
        response.setDescription(flow.getDescription());
        response.setParams(flow.getContextConfig());
        response.setStatus(flow.getStatus());
        return R.ok(response);
    }

    /**
     * 返回运行启动参数,flowInstance.input
     * 以及flowInstance.output
     * @param id
     * @return
     */
    @GetMapping("/instanceDetail/{id}")
    public R<FlowInstanceDetailResponse> instanceDetail(@PathVariable Long id) {
        FlowInstance instance = flowInstanceService.detail(id);
        FlowInstanceDetailResponse response = new FlowInstanceDetailResponse();
        response.setInput(instance.getInput());
        response.setOutput(instance.getOutput());
        return R.ok(response);
    }

    @GetMapping("/instanceRecords/{flowInstanceId}")
    public R<List<NodeExecutorRecordSimpleResponse>> instanceRecords(@PathVariable Long flowInstanceId) {
        return R.ok(nodeExecutorRecordService.listSimpleByFlowInstanceId(flowInstanceId));
    }

    /**
    *传入NodeExecutorRecordId，返回字段:
    nodeId,nodeKey,status,startTime,endTime,input,outpu
     */
    @GetMapping("/instanceRecordDetail/{id}")
    public R<NodeExecutorRecordDetailResponse> instanceRecordDetail(@PathVariable Long id) {
        var record = nodeExecutorRecordService.detail(id);
        NodeExecutorRecordDetailResponse response = new NodeExecutorRecordDetailResponse();
        response.setNodeId(record.getNodeId());
        response.setNodeKey(record.getNodeKey());
        response.setStatus(record.getStatus());
        response.setStartTime(record.getStartTime());
        response.setEndTime(record.getEndTime());
        response.setInput(record.getInput());
        response.setOutput(record.getOutput());
        return R.ok(response);
    }
}
