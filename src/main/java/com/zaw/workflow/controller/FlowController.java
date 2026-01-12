package com.zaw.workflow.controller;


import com.zaw.common.web.R;
import com.zaw.workflow.engine.FlowEngine;
import com.zaw.workflow.entity.Flow;
import com.zaw.workflow.entity.FlowInstance;
import com.zaw.workflow.service.FlowInstanceService;
import com.zaw.workflow.service.FlowService;
import com.zaw.workflow.web.CreateFlowRequest;
import com.zaw.workflow.web.FlowExecRequest;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author lizhong.zhang@yituishui.com
 * @since 2026/1/9
 */
@RequestMapping("/flow")
@RestController
@AllArgsConstructor
public class FlowController {

    private final FlowService flowService;

    private final FlowInstanceService flowInstanceService;

    private final FlowEngine flowEngine;

    /**
     * 创建草稿流程
     * @param request 创建流程请求
     * @return 流程实体
     */
    @PostMapping
    public R<Flow> create(@RequestBody CreateFlowRequest request) {
        return R.ok(flowService.create(request));
    }

    /**
     * 获取流程详情
     * @param id 流程ID
     * @return 流程实体
     */
    @GetMapping("/{id}")
    public R<Flow> detail(@PathVariable Long id) {
        return R.ok(flowService.detail(id));
    }

    /**
     * 获取流程列表
     * @param name 流程名称
     * @return 流程列表
     */
    @GetMapping
    public R<List<Flow>> list(@RequestParam(required = false) String name) {
        return R.ok(flowService.list(name));
    }

    /**
     * 删除草稿流程
     * @param id 流程ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public R<Flow> delete(@PathVariable Long id) {
        return R.ok(flowService.deleteDraft(id));
    }

    /**
     * 发布流程
     * @param id 流程ID
     * @return 发布结果
     */
    @PostMapping("/{id}/publish")
    public R<Flow> publish(@PathVariable Long id) {
        return R.ok(flowService.publish(id));
    }

    /**
     * 克隆流程
     * @param id 流程ID
     * @return 克隆结果
     */
    @PostMapping("/{id}/clone")
    public R<Flow> clone(@PathVariable Long id) {
        return R.ok(flowService.cloneFlow(id));
    }

    /**
     * 获取最近一次执行流程的入参
     * @param flowId
     * @return
     */
     @GetMapping("/{flowId}/latest-instance-input")
    public R<String> getLatestInstanceInput(@PathVariable Long flowId) {
        FlowInstance latestInstance = flowInstanceService.getLatestByFlowId(flowId);
        if (latestInstance != null) {
            return R.ok(latestInstance.getInput());
        } else {
            return R.fail("No instances found for flow ID: " + flowId);
        }
    }


        /**
     * 启动流程实例（最简同步执行）。
     */
    @PostMapping("/start")
    public R<FlowInstance> start(@RequestBody FlowExecRequest req) {
        return
                R.ok(flowEngine.start(req));
    }

    @PostMapping("/continueTask")
    public R<FlowInstance> continueTask(@RequestBody FlowExecRequest req) {
        return R.ok(flowEngine.continueTask(req));
    }
}
