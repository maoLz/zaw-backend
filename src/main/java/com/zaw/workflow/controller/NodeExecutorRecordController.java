package com.zaw.workflow.controller;

import com.zaw.common.web.R;
import com.zaw.workflow.entity.NodeExecutorRecord;
import com.zaw.workflow.enums.ExecutorStatus;
import com.zaw.workflow.service.NodeExecutorRecordService;
import com.zaw.workflow.web.HumanInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/node-executor-records")
@RequiredArgsConstructor
public class NodeExecutorRecordController {

    private final NodeExecutorRecordService nodeExecutorRecordService;

    /**
     * 创建执行记录
     * @param record 执行记录
     * @return 执行记录
     */
    @PostMapping
    public R<NodeExecutorRecord> create(@RequestBody NodeExecutorRecord record) {
        return R.ok(nodeExecutorRecordService.create(record));
    }

    /**
     * 重试执行记录
     * @param id 记录ID
     * @return 操作结果
     */
    @PostMapping("/re-execute/{id}")
    public R<String> reExecute(@PathVariable Long id) {
        nodeExecutorRecordService.reExecute(id);
        return R.ok("重试成功");
    }

    /**
     * 获取全部执行记录
     * @return 执行记录列表
     */
    @GetMapping
    public R<List<NodeExecutorRecord>> list() {
        return R.ok(nodeExecutorRecordService.list());
    }

    /**
     * 获取执行记录详情
     * @param id 记录ID
     * @return 执行记录
     */
    @GetMapping("/{id}")
    public R<NodeExecutorRecord> detail(@PathVariable Long id) {
        return R.ok(nodeExecutorRecordService.detail(id));
    }

    /**
     * 获取人工节点信息
     * @param id 记录ID
     * @return 人工节点信息
     */
    @GetMapping("/human-info/{id}")
    public R<HumanInfoResponse> getHumanInfo(@PathVariable Long id) {
        return R.ok(nodeExecutorRecordService.getHumanInfo(id));
    }

    /**
     * 根据实例ID获取执行记录
     * @param flowInstanceId 实例ID
     * @return 执行记录列表
     */
    @GetMapping("/flow-instance/{flowInstanceId}")
    public R<List<NodeExecutorRecord>> listByFlowInstanceId(@PathVariable Long flowInstanceId) {
        return R.ok(nodeExecutorRecordService.listByFlowInstanceId(flowInstanceId));
    }

    /**
     * 根据实例ID和状态获取执行记录
     * @param flowInstanceId 实例ID
     * @param status 状态
     * @return 执行记录列表
     */
    @GetMapping("/flow-instance/{flowInstanceId}/status/{status}")
    public R<List<NodeExecutorRecord>> listByFlowInstanceIdAndStatus(
            @PathVariable Long flowInstanceId,
            @PathVariable String status
    ) {
        ExecutorStatus statusEnum = ExecutorStatus.valueOf(status.toUpperCase());
        return R.ok(nodeExecutorRecordService.listByFlowInstanceIdAndStatus(flowInstanceId, statusEnum));
    }

    /**
     * 更新执行记录
     * @param id 记录ID
     * @param record 执行记录
     * @return 更新后记录
     */
    @PutMapping("/{id}")
    public R<NodeExecutorRecord> update(@PathVariable Long id, @RequestBody NodeExecutorRecord record) {
        return R.ok(nodeExecutorRecordService.update(id, record));
    }

    /**
     * 删除执行记录
     * @param id 记录ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        nodeExecutorRecordService.delete(id);
        return R.ok("删除成功");
    }

    /**
     * 根据实例ID删除执行记录
     * @param flowInstanceId 实例ID
     * @return 删除结果
     */
    @DeleteMapping("/flow-instance/{flowInstanceId}")
    public R<String> deleteByFlowInstanceId(@PathVariable Long flowInstanceId) {
        nodeExecutorRecordService.deleteByFlowInstanceId(flowInstanceId);
        return R.ok("删除成功");
    }
}
