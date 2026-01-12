package com.zaw.workflow.service;

import com.zaw.common.exception.BizException;
import com.zaw.workflow.entity.Flow;
import com.zaw.workflow.entity.FlowEdge;
import com.zaw.workflow.entity.FlowNode;
import com.zaw.workflow.enums.NodeType;
import com.zaw.workflow.mapper.FlowNodeMapper;
import com.zaw.workflow.repository.FlowEdgeRepository;
import com.zaw.workflow.repository.FlowNodeRepository;
import com.zaw.workflow.repository.FlowRepository;
import com.zaw.workflow.web.CreateFlowNodeRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author lizhong.zhang@yituishui.com
 * @since 2026/1/9
 */
@Service
@AllArgsConstructor
public class FlowNodeService {

    private final FlowNodeRepository flowNodeRepository;
    private final FlowRepository flowRepository;
    private final FlowEdgeRepository flowEdgeRepository;

    /**
     * 创建流程节点
     *
     * @param request 创建节点请求
     * @return 节点实体
     */
    @Transactional
    public FlowNode create(CreateFlowNodeRequest request) {
        // 第一步：校验流程是否存在
        Flow flow = flowRepository.findById(request.getFlowId()).orElse(null);
        if (flow == null) {
            // 流程不存在时抛出业务异常
            throw new BizException("流程不存在");
        }
        // 第二步：保存节点数据
        FlowNode node = FlowNodeMapper.toEntity(request);
        node.setFlowId(request.getFlowId());
        return flowNodeRepository.save(node);
    }

    /**
     * 追加末尾节点
     *
     * @param request 追加节点请求
     * @return 新增节点
     */
    @Transactional
    public FlowNode addLastNode(CreateFlowNodeRequest request) {
        // 第一步：校验流程是否存在
        Flow flow = flowRepository.findById(request.getFlowId()).orElse(null);
        if (flow == null) {
            // 流程不存在时抛出业务异常
            throw new BizException("流程不存在");
        }
        // 第二步：查找结束节点
        List<FlowNode> flowNodes = flowNodeRepository.findByFlowId(request.getFlowId());
        FlowNode endNode = flowNodes.stream()
                .filter(node -> node.getNodeType() == NodeType.END)
                .findFirst()
                .orElseThrow(() -> new BizException("未找到结束节点"));
        // 第三步：找到指向结束节点的连线
        FlowEdge edgeToEnd = flowEdgeRepository.findByFlowIdAndToNodeId(request.getFlowId(), endNode.getId())
                .orElseThrow(() -> new BizException("未找到结束节点连线"));
        // 第四步：获取末尾业务节点
        FlowNode lastBusinessNode = flowNodeRepository.findById(edgeToEnd.getFromNodeId()).orElse(null);
        if (lastBusinessNode == null) {
            // 末尾业务节点不存在时抛出业务异常
            throw new BizException("末尾节点不存在");
        }
        // 第五步：创建新节点
        FlowNode newNode = FlowNodeMapper.toEntity(request);
        newNode.setFlowId(request.getFlowId());
        FlowNode savedNode = flowNodeRepository.save(newNode);
        // 第六步：建立新的连线关系
        FlowEdge newEdgeFromBusiness = new FlowEdge();
        newEdgeFromBusiness.setFlowId(request.getFlowId());
        newEdgeFromBusiness.setFromNodeId(lastBusinessNode.getId());
        newEdgeFromBusiness.setToNodeId(savedNode.getId());
        flowEdgeRepository.save(newEdgeFromBusiness);
        FlowEdge newEdgeToEnd = new FlowEdge();
        newEdgeToEnd.setFlowId(request.getFlowId());
        newEdgeToEnd.setFromNodeId(savedNode.getId());
        newEdgeToEnd.setToNodeId(endNode.getId());
        flowEdgeRepository.save(newEdgeToEnd);
        // 第七步：删除原有连线
        flowEdgeRepository.delete(edgeToEnd);
        return savedNode;
    }


    /**
     * 获取节点详情
     *
     * @param id 节点ID
     * @return 节点实体
     */
    public FlowNode detail(Long id) {
        // 第一步：查询节点详情
        FlowNode node = flowNodeRepository.findById(id).orElse(null);
        if (node == null) {
            // 节点不存在时抛出业务异常
            throw new BizException("节点不存在");
        }
        return node;
    }

    /**
     * 根据流程ID获取节点列表
     *
     * @param flowId 流程ID
     * @return 节点列表
     */
    public List<FlowNode> listByFlowId(Long flowId) {
        // 第一步：查询流程下的节点
        return flowNodeRepository.findByFlowId(flowId);
    }

    /**
     * 更新流程节点
     *
     * @param id 节点ID
     * @param request 更新节点请求
     * @return 更新后节点
     */
    @Transactional
    public FlowNode update(Long id, CreateFlowNodeRequest request) {
        // 第一步：查询节点
        FlowNode node = flowNodeRepository.findById(id).orElse(null);
        if (node == null) {
            // 节点不存在时抛出业务异常
            throw new BizException("节点不存在");
        }
        // 第二步：更新节点字段
        node.setFlowId(request.getFlowId());
        node.setNodeKey(request.getNodeKey());
        node.setNodeName(request.getNodeName());
        node.setNodeType(request.getNodeType());
        node.setExecutorType(request.getExecutorType());
        node.setConfig(request.getConfig());
        node.setPositionX(request.getPositionX());
        node.setPositionY(request.getPositionY());
        return flowNodeRepository.save(node);
    }

    /**
     * 删除流程节点
     *
     * @param id 节点ID
     */
    @Transactional
    public void delete(Long id) {
        // 第一步：检查节点是否存在
        boolean exists = flowNodeRepository.existsById(id);
        if (!exists) {
            // 节点不存在时抛出业务异常
            throw new BizException("节点不存在");
        }
        // 第二步：删除节点
        flowNodeRepository.deleteById(id);
    }

    /**
     * 根据流程ID删除节点
     *
     * @param flowId 流程ID
     */
    @Transactional
    public void deleteByFlowId(Long flowId) {
        // 第一步：删除流程下所有连线
        flowEdgeRepository.deleteByFlowId(flowId);
        // 第二步：删除流程下所有节点
        flowNodeRepository.deleteByFlowId(flowId);
    }
}
