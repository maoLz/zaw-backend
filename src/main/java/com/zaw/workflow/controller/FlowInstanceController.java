package com.zaw.workflow.controller;

import com.zaw.common.web.R;
import com.zaw.workflow.entity.FlowInstance;
import com.zaw.workflow.service.FlowInstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/flow-instances")
@RequiredArgsConstructor
public class FlowInstanceController {

    private final FlowInstanceService flowInstanceService;

    /**
     * 获取流程实例详情
     * @param id 实例ID
     * @return 流程实例
     */
    @GetMapping("/{id}")
    public R<FlowInstance> detail(@PathVariable Long id) {
        return R.ok(flowInstanceService.detail(id));
    }

    /**
     * 根据流程ID获取实例列表
     * @param flowId 流程ID
     * @return 实例列表
     */
    @GetMapping("/flow/{flowId}")
    public R<List<FlowInstance>> listByFlowId(@PathVariable Long flowId) {
        return R.ok(flowInstanceService.listByFlowId(flowId));
    }

    /**
     * 根据流程ID删除实例
     * @param flowId 流程ID
     * @return 删除结果
     */
    @DeleteMapping("/flow/{flowId}")
    public R<String> deleteByFlowId(@PathVariable Long flowId) {
        flowInstanceService.deleteByFlowId(flowId);
        return R.ok("删除成功");
    }

    /**
     * 删除流程实例
     * @param id 实例ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        flowInstanceService.delete(id);
        return R.ok("删除成功");
    }

    
}
