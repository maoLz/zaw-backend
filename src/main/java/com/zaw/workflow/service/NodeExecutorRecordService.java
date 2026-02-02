package com.zaw.workflow.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.zaw.common.exception.BizException;
import com.zaw.workflow.entity.FlowEdge;
import com.zaw.workflow.entity.FlowNode;
import com.zaw.workflow.entity.NodeExecutorRecord;
import com.zaw.workflow.enums.ExecutorStatus;
import com.zaw.workflow.repository.FlowEdgeRepository;
import com.zaw.workflow.repository.FlowNodeRepository;
import com.zaw.workflow.repository.NodeExecutorRecordRepository;
import com.zaw.workflow.web.HumanInfoResponse;
import com.zaw.workflow.web.NodeExecutorRecordSimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NodeExecutorRecordService {

    private final NodeExecutorRecordRepository nodeExecutorRecordRepository;
    private final FlowNodeRepository flowNodeRepository;
    private final FlowEdgeRepository flowEdgeRepository;

    /**
     * 创建执行记录
     * @param record 执行记录
     * @return 执行记录
     */
    @Transactional
    public NodeExecutorRecord create(NodeExecutorRecord record) {
        return nodeExecutorRecordRepository.save(record);
    }

    /**
     * 获取执行记录详情
     * @param id 记录ID
     * @return 执行记录
     */
    public NodeExecutorRecord detail(Long id) {
        NodeExecutorRecord record = nodeExecutorRecordRepository.findById(id).orElse(null);
        if (record == null) {
            throw new BizException("执行记录不存在");
        }
        return record;
    }

    /**
     * 获取全部执行记录
     * @return 执行记录列表
     */
    public List<NodeExecutorRecord> list() {
        return nodeExecutorRecordRepository.findAll();
    }

    /**
     * 重新执行记录
     * @param id 记录ID
     */
    public void reExecute(Long id) {
        NodeExecutorRecord record = detail(id);
        if (record.getStatus() == ExecutorStatus.RUNNING) {
            throw new BizException("执行中记录不支持重试");
        }
        throw new BizException("当前版本暂不支持重试执行");
    }

    /**
     * 根据实例ID获取执行记录
     * @param flowInstanceId 实例ID
     * @return 执行记录列表
     */
    public List<NodeExecutorRecord> listByFlowInstanceId(Long flowInstanceId) {
        return nodeExecutorRecordRepository.findByFlowInstanceIdOrderByIdAsc(flowInstanceId);
    }

    /**
     * 获取执行记录简单字段列表
     * @param flowInstanceId 实例ID
     * @return 简单字段列表
     */
    public List<NodeExecutorRecordSimpleResponse> listSimpleByFlowInstanceId(Long flowInstanceId) {
        return nodeExecutorRecordRepository.findSimpleByFlowInstanceIdOrderByIdAsc(flowInstanceId);
    }

    /**
     * 根据实例ID与状态获取执行记录
     * @param flowInstanceId 实例ID
     * @param status 状态
     * @return 执行记录列表
     */
    public List<NodeExecutorRecord> listByFlowInstanceIdAndStatus(Long flowInstanceId, ExecutorStatus status) {
        return nodeExecutorRecordRepository.findByFlowInstanceIdAndStatusOrderByIdAsc(flowInstanceId, status);
    }

    /**
     * 更新执行记录
     * @param id 记录ID
     * @param recordDetails 更新内容
     * @return 更新后记录
     */
    @Transactional
    public NodeExecutorRecord update(Long id, NodeExecutorRecord recordDetails) {
        NodeExecutorRecord record = detail(id);
        record.setFlowInstanceId(recordDetails.getFlowInstanceId());
        record.setNodeId(recordDetails.getNodeId());
        record.setNodeKey(recordDetails.getNodeKey());
        record.setStatus(recordDetails.getStatus());
        record.setInput(recordDetails.getInput());
        record.setOutput(recordDetails.getOutput());
        record.setErrorMessage(recordDetails.getErrorMessage());
        record.setStartTime(recordDetails.getStartTime());
        record.setEndTime(recordDetails.getEndTime());
        record.setRetryCount(recordDetails.getRetryCount());
        record.setAction(recordDetails.getAction());
        record.setNextNodeId(recordDetails.getNextNodeId());
        return nodeExecutorRecordRepository.save(record);
    }

    /**
     * 删除执行记录
     * @param id 记录ID
     */
    @Transactional
    public void delete(Long id) {
        if (!nodeExecutorRecordRepository.existsById(id)) {
            throw new BizException("执行记录不存在");
        }
        nodeExecutorRecordRepository.deleteById(id);
    }

    /**
     * 根据实例ID删除执行记录
     * @param flowInstanceId 实例ID
     */
    @Transactional
    public void deleteByFlowInstanceId(Long flowInstanceId) {
        nodeExecutorRecordRepository.deleteByFlowInstanceId(flowInstanceId);
    }

    /**
     * 获取人工节点展示信息
     * @param id 记录ID
     * @return 人工节点信息
     */
    public HumanInfoResponse getHumanInfo(Long id) {
        NodeExecutorRecord record = detail(id);
        FlowNode current = flowNodeRepository.findById(record.getNodeId()).orElse(null);
        if (current == null) {
            throw new BizException("节点不存在");
        }
        List<String> formSchemaNames = new ArrayList<>();
        if (current.getConfig() != null) {
            JSONObject config = JSONObject.parseObject(current.getConfig());
            if (config != null) {
                JSONArray formSchema = config.getJSONArray("formSchema");
                if (formSchema != null) {
                    for (Object item : formSchema) {
                        if (item instanceof JSONObject obj) {
                            String name = obj.getString("name");
                            if (name != null && !name.isBlank()) {
                                formSchemaNames.add(name);
                            }
                        }
                    }
                }
            }
        }
        List<FlowEdge> edges = flowEdgeRepository.findByFlowId(current.getFlowId());
        Set<Long> nextNodeIds = new HashSet<>();
        for (FlowEdge edge : edges) {
            if (current.getId().equals(edge.getFromNodeId()) && edge.getToNodeId() != null) {
                nextNodeIds.add(edge.getToNodeId());
            }
        }
        List<FlowNode> flowNodes = flowNodeRepository.findByFlowId(current.getFlowId());
        Map<Long, FlowNode> nodeMap = new HashMap<>();
        for (FlowNode node : flowNodes) {
            nodeMap.put(node.getId(), node);
        }
        List<HumanInfoResponse.NextNode> options = new ArrayList<>();
        for (Long nextNodeId : nextNodeIds) {
            FlowNode node = nodeMap.get(nextNodeId);
            if (node == null) {
                continue;
            }
            HumanInfoResponse.NextNode nextNode = new HumanInfoResponse.NextNode();
            nextNode.setNodeKey(node.getNodeKey());
            nextNode.setLabel(node.getNodeName());
            options.add(nextNode);
        }
        HumanInfoResponse response = new HumanInfoResponse();
        response.setFlowInstanceId(record.getFlowInstanceId());
        response.setNodeId(record.getNodeId());
        response.setFormSchema(formSchemaNames);
        response.setOptions(options);
        return response;
    }
}
