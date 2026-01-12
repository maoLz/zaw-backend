package com.zaw.workflow.controller;


import com.zaw.common.web.R;
import com.zaw.workflow.entity.FlowNode;
import com.zaw.workflow.service.FlowService;
import com.zaw.workflow.service.FlowNodeService;
import com.zaw.workflow.web.FlowCanvasRequest;
import com.zaw.workflow.web.FlowCanvasResult;
import com.zaw.workflow.web.CreateFlowNodeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author lizhong.zhang@yituishui.com
 * @since 2026/1/9
 */
@RestController
@RequestMapping("/flow-nodes")
@RequiredArgsConstructor
public class FlowNodeController {

    private final FlowNodeService flowNodeService;
    private final FlowService flowService;

    /**
     * 创建流程节点
     * @param request 创建节点请求
     * @return 流程节点
     */
    @PostMapping
    public R<FlowNode> create(@RequestBody CreateFlowNodeRequest request) {
        return R.ok(flowNodeService.create(request));
    }

    /**
     * 追加末尾节点
     * @param request 追加节点请求
     * @return 新增节点
     */
    @PostMapping("/last")
    public R<FlowNode> addLastNode(@RequestBody CreateFlowNodeRequest request) {
        return R.ok(flowNodeService.addLastNode(request));
    }

    /**
     * 获取流程节点详情
     * @param id 节点ID
     * @return 流程节点
     */
    @GetMapping("/{id}")
    public R<FlowNode> detail(@PathVariable Long id) {
        return R.ok(flowNodeService.detail(id));
    }

    /**
     * 根据流程ID获取节点列表
     * @param flowId 流程ID
     * @return 节点列表
     */
    @GetMapping("/flow/{flowId}")
    public R<List<FlowNode>> listByFlowId(@PathVariable Long flowId) {
        return R.ok(flowNodeService.listByFlowId(flowId));
    }

    /**
     * 更新流程节点
     * @param id 节点ID
     * @param request 更新节点请求
     * @return 更新后节点
     */
    @PutMapping("/{id}")
    public R<FlowNode> update(@PathVariable Long id, @RequestBody CreateFlowNodeRequest request) {
        return R.ok(flowNodeService.update(id, request));
    }

    /**
     * 删除流程节点
     * @param id 节点ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        flowNodeService.delete(id);
        return R.ok("删除成功");
    }

    /**
     * 根据流程ID删除节点
     * @param flowId 流程ID
     * @return 删除结果
     */
    @DeleteMapping("/flow/{flowId}")
    public R<String> deleteByFlowId(@PathVariable Long flowId) {
        flowNodeService.deleteByFlowId(flowId);
        return R.ok("删除成功");
    }

    /**
     * 提交流程画布
     * @param request 画布请求
     * @return 提交结果
     */
    @PostMapping("/canvas/submit")
    public R<String> submit(@RequestBody FlowCanvasRequest request) {
        flowService.updateCanvas(request);
        return R.ok();
    }

    /**
     * 获取流程画布
     * @param flowId 流程ID
     * @return 画布结果
     */
    @GetMapping("/canvas/{flowId}")
    public R<FlowCanvasResult> loadCanvas(@PathVariable Long flowId) {
        return R.ok(flowService.loadCanvas(flowId));
    }


}
