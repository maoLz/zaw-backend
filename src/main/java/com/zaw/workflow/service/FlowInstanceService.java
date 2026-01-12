package com.zaw.workflow.service;

import com.zaw.common.exception.BizException;
import com.zaw.workflow.entity.FlowInstance;
import com.zaw.workflow.enums.FlowInstanceStatus;
import com.zaw.workflow.repository.FlowInstanceRepository;
import com.zaw.workflow.repository.NodeExecutorRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlowInstanceService {

    private final FlowInstanceRepository flowInstanceRepository;
    private final NodeExecutorRecordRepository nodeExecutorRecordRepository;

    /**
     * 获取流程实例详情
     *
     * @param id 实例ID
     * @return 流程实例
     */
    public FlowInstance detail(Long id) {
        FlowInstance flowInstance = flowInstanceRepository.findById(id).orElse(null);
        if (flowInstance == null) {
            throw new BizException("流程实例不存在");
        }
        return flowInstance;
    }

    /**
     * 根据流程ID获取实例列表
     *
     * @param flowId 流程ID
     * @return 实例列表
     */
    public List<FlowInstance> listByFlowId(Long flowId) {
        return flowInstanceRepository.findByFlowId(flowId);
    }

    /**
     * 根据状态获取实例列表
     *
     * @param status 流程实例状态
     * @return 实例列表
     */
    public List<FlowInstance> listByStatus(FlowInstanceStatus status) {
        return flowInstanceRepository.findByStatus(status);
    }

    /**
     * 根据业务标识获取实例列表
     *
     * @param businessKey 业务标识
     * @return 实例列表
     */
    public List<FlowInstance> listByBusinessKey(String businessKey) {
        return flowInstanceRepository.findByBusinessKey(businessKey);
    }

    /**
     * 删除流程实例
     *
     * @param id 实例ID
     */
    @Transactional
    public void delete(Long id) {
        if (!flowInstanceRepository.existsById(id)) {
            throw new BizException("流程实例不存在");
        }
        nodeExecutorRecordRepository.deleteByFlowInstanceId(id);
        flowInstanceRepository.deleteById(id);
    }

    /**
     * 删除流程下的所有实例与执行记录
     *
     * @param flowId 流程ID
     */
    @Transactional
    public void deleteByFlowId(Long flowId) {
        List<FlowInstance> flowInstances = flowInstanceRepository.findByFlowId(flowId);
        if (flowInstances.isEmpty()) {
            return;
        }
        List<Long> instanceIds = new ArrayList<>(flowInstances.size());
        for (FlowInstance instance : flowInstances) {
            instanceIds.add(instance.getId());
        }
        nodeExecutorRecordRepository.deleteByFlowInstanceIdIn(instanceIds);
        flowInstanceRepository.deleteByFlowId(flowId);
    }

    public FlowInstance getLatestByFlowId(Long flowId) {
        return flowInstanceRepository.findTopByFlowIdOrderByCreateDateDesc(flowId)
                .orElse(null);
    }
}
