package com.zaw.workflow.service;


import com.zaw.common.exception.BizException;
import com.zaw.workflow.entity.Flow;
import com.zaw.workflow.entity.FlowEdge;
import com.zaw.workflow.entity.FlowNode;
import com.zaw.workflow.enums.ExecutorType;
import com.zaw.workflow.enums.FlowStatus;
import com.zaw.workflow.enums.NodeType;
import com.zaw.workflow.mapper.FlowEdgeMapper;
import com.zaw.workflow.mapper.FlowMapper;
import com.zaw.workflow.mapper.FlowNodeMapper;
import com.zaw.workflow.repository.FlowEdgeRepository;
import com.zaw.workflow.repository.FlowNodeRepository;
import com.zaw.workflow.repository.FlowRepository;
import com.zaw.workflow.web.FlowCanvasRequest;
import com.zaw.workflow.web.FlowCanvasResult;
import com.zaw.workflow.web.CreateFlowEdgeRequest;
import com.zaw.workflow.web.CreateFlowNodeRequest;
import com.zaw.workflow.web.CreateFlowRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author lizhong.zhang@yituishui.com
 * @since 2026/1/9
 */
@Service
@AllArgsConstructor
public class FlowService {

    private final FlowRepository flowRepository;

    private final FlowNodeRepository flowNodeRepository;

    private final FlowEdgeRepository flowEdgeRepository;


    /**
     * 创建草稿流程
     *
     * @param request 创建流程请求
     * @return 流程实体
     */
    public Flow create(CreateFlowRequest request) {
        // 第一步：保存流程基础信息
        Flow flow = flowRepository.save(FlowMapper.toEntity(request));
        // 第二步：创建开始节点
        CreateFlowNodeRequest start = CreateFlowNodeRequest.builder()
                .nodeKey("start")
                .nodeType(NodeType.START)
                .executorType(ExecutorType.NONE)
                .nodeName("start")
                .flowId(flow.getId())
                .build();
        FlowNode startNode = flowNodeRepository.save(FlowNodeMapper.toEntity(start));
        // 第三步：创建结束节点
        CreateFlowNodeRequest end = CreateFlowNodeRequest.builder()
                .nodeKey("end")
                .nodeType(NodeType.END)
                .executorType(ExecutorType.NONE)
                .nodeName("end")
                .flowId(flow.getId())
                .build();
        FlowNode endNode = flowNodeRepository.save(FlowNodeMapper.toEntity(end));
        // 第四步：建立开始到结束的连线
        CreateFlowEdgeRequest edge = CreateFlowEdgeRequest.builder()
                .fromNodeId(startNode.getId())
                .toNodeId(endNode.getId())
                .flowId(flow.getId())
                .build();
        flowEdgeRepository.save(FlowEdgeMapper.toEntity(edge));
        return flow;
    }

    /**
     * 获取流程详情
     *
     * @param id 流程ID
     * @return 流程实体
     */
    public Flow detail(Long id) {
        // 第一步：查询流程详情
        Flow flow = flowRepository.findById(id).orElse(null);
        if (flow == null) {
            // 流程不存在时抛出业务异常
            throw new BizException("流程不存在");
        }
        return flow;
    }

    /**
     * 获取流程列表
     *
     * @param name 流程名称
     * @return 流程列表
     */
    public List<Flow> list(String name) {
        // 第一步：判断是否需要名称筛选
        if (!StringUtils.hasText(name)) {
            // 名称为空时返回全部流程
            return flowRepository.findAll();
        } else {
            // 名称不为空时按名称模糊查询
            return flowRepository.findByNameContaining(name);
        }
    }

    /**
     * 删除草稿流程
     *
     * @param id 流程ID
     * @return 操作结果
     */
    public Flow deleteDraft(Long id) {
        // 第一步：查询流程
        Flow flow = flowRepository.findById(id).orElse(null);
        if (flow == null) {
            // 流程不存在时抛出业务异常
            throw new BizException("流程不存在");
        }
        if (flow.getStatus() != FlowStatus.DRAFT) {
            // 非草稿流程不允许删除
            throw new BizException("仅支持删除草稿流程");
        }
        // 第二步：删除流程
        flowRepository.delete(flow);
        return flow;
    }

    /**
     * 发布流程
     *
     * @param id 流程ID
     * @return 操作结果
     */
    public Flow publish(Long id) {
        // 第一步：查询流程
        Flow flow = flowRepository.findById(id).orElse(null);
        if (flow == null) {
            // 流程不存在时抛出业务异常
            throw new BizException("流程不存在");
        }
        // 第二步：更新流程状态
        flow.setStatus(FlowStatus.PUBLISHED);
        return flowRepository.save(flow);
    }

    /**
     * 克隆流程
     *
     * @param id 流程ID
     * @return 操作结果
     */
    @Transactional
    public Flow cloneFlow(Long id) {
        // 第一步：查询原始流程
        Flow source = flowRepository.findById(id).orElse(null);
        if (source == null) {
            // 原始流程不存在时抛出业务异常
            throw new BizException("流程不存在");
        }
        // 第二步：计算新版本号
        Integer nextVersion = resolveNextVersion(source.getCode());
        // 第三步：复制流程基础信息
        Flow cloned = new Flow();
        cloned.setName(source.getName());
        cloned.setCode(source.getCode());
        cloned.setDescription(source.getDescription());
        cloned.setContextConfig(source.getContextConfig());
        cloned.setStatus(FlowStatus.DRAFT);
        cloned.setFlowVersion(nextVersion);
        Flow savedFlow = flowRepository.save(cloned);
        // 第四步：复制流程节点
        List<FlowNode> nodes = flowNodeRepository.findByFlowId(source.getId());
        Map<Long, Long> nodeIdMapping = new HashMap<>();
        for (FlowNode node : nodes) {
            FlowNode clonedNode = new FlowNode();
            clonedNode.setFlowId(savedFlow.getId());
            clonedNode.setNodeKey(node.getNodeKey());
            clonedNode.setNodeName(node.getNodeName());
            clonedNode.setNodeType(node.getNodeType());
            clonedNode.setExecutorType(node.getExecutorType());
            clonedNode.setConfig(node.getConfig());
            clonedNode.setPositionX(node.getPositionX());
            clonedNode.setPositionY(node.getPositionY());
            FlowNode savedNode = flowNodeRepository.save(clonedNode);
            nodeIdMapping.put(node.getId(), savedNode.getId());
        }
        // 第五步：复制流程连线
        List<FlowEdge> edges = flowEdgeRepository.findByFlowId(source.getId());
        for (FlowEdge edge : edges) {
            FlowEdge clonedEdge = new FlowEdge();
            clonedEdge.setFlowId(savedFlow.getId());
            clonedEdge.setConditionExpression(edge.getConditionExpression());
            clonedEdge.setFromNodeId(nodeIdMapping.get(edge.getFromNodeId()));
            clonedEdge.setToNodeId(nodeIdMapping.get(edge.getToNodeId()));
            flowEdgeRepository.save(clonedEdge);
        }
        return savedFlow;
    }

    /**
     * 计算流程新版本号
     *
     * @param code 流程编码
     * @return 新版本号
     */
    private Integer resolveNextVersion(String code) {
        // 第一步：查询最新版本流程
        Optional<Flow> latest = flowRepository.findTopByCodeOrderByFlowVersionDesc(code);
        if (latest.isEmpty()) {
            // 没有历史版本时从 1 开始
            return 1;
        } else {
            // 已存在版本时递增
            return latest.get().getFlowVersion() + 1;
        }
    }

    /**
     * 更新流程画布
     *
     * @param request 画布请求
     */
    @Transactional
    public void updateCanvas(FlowCanvasRequest request) {
        // 第一步：校验流程ID
        if (request.getFlowId() == null) {
            // 流程ID为空时抛出业务异常
            throw new BizException("流程ID不能为空");
        }
        // 第二步：校验流程是否存在
        Flow flow = flowRepository.findById(request.getFlowId()).orElse(null);
        if (flow == null) {
            // 流程不存在时抛出业务异常
            throw new BizException("流程不存在");
        }
        // 第三步：收集画布涉及的节点ID
        Set<Long> nodeIds = new HashSet<>();
        if (request.getNodes() != null) {
            // 节点列表存在时收集节点ID
            for (FlowCanvasRequest.NodeRq node : request.getNodes()) {
                if (node.getId() != null) {
                    // 节点ID不为空时加入集合
                    nodeIds.add(node.getId());
                }
            }
        }
        if (request.getEdges() != null) {
            // 连线列表存在时收集节点ID
            for (FlowCanvasRequest.EdgeRq edge : request.getEdges()) {
                if (edge.getFrom() != null) {
                    // 起点不为空时加入集合
                    nodeIds.add(edge.getFrom());
                }
                if (edge.getTo() != null) {
                    // 终点不为空时加入集合
                    nodeIds.add(edge.getTo());
                }
            }
        }
        // 第四步：查询并校验节点归属
        List<FlowNode> nodes = nodeIds.isEmpty() ? new ArrayList<>() : flowNodeRepository.findAllById(nodeIds);
        Map<Long, FlowNode> nodeMap = new HashMap<>();
        for (FlowNode node : nodes) {
            nodeMap.put(node.getId(), node);
        }
        for (Long nodeId : nodeIds) {
            FlowNode node = nodeMap.get(nodeId);
            if (node == null) {
                // 节点不存在时抛出业务异常
                throw new BizException("节点不存在");
            }
            if (!node.getFlowId().equals(flow.getId())) {
                // 节点不属于当前流程时抛出业务异常
                throw new BizException("节点不属于当前流程");
            }
        }
        // 第五步：更新节点坐标
        List<FlowNode> updateNodes = new ArrayList<>();
        if (request.getNodes() != null) {
            // 节点列表存在时更新坐标
            for (FlowCanvasRequest.NodeRq nodeRq : request.getNodes()) {
                FlowNode node = nodeMap.get(nodeRq.getId());
                if (node != null) {
                    // 节点存在时更新坐标
                    node.setPositionX(nodeRq.getX());
                    node.setPositionY(nodeRq.getY());
                    updateNodes.add(node);
                }
            }
        }
        if (!updateNodes.isEmpty()) {
            // 有需要更新的节点时批量保存
            flowNodeRepository.saveAll(updateNodes);
        } else {
            // 没有需要更新的节点时不执行保存
        }
        // 第六步：重建连线关系
        flowEdgeRepository.deleteByFlowId(request.getFlowId());
        if (request.getEdges() != null) {
            // 连线列表存在时重建连线
            List<FlowEdge> edges = new ArrayList<>();
            for (FlowCanvasRequest.EdgeRq edgeRq : request.getEdges()) {
                FlowNode sourceNode = nodeMap.get(edgeRq.getFrom());
                FlowNode targetNode = nodeMap.get(edgeRq.getTo());
                if (sourceNode == null || targetNode == null) {
                    // 连线节点不存在时抛出业务异常
                    throw new BizException("连线节点不存在");
                } else {
                    // 连线节点存在时创建连线
                    FlowEdge edge = new FlowEdge();
                    edge.setFlowId(request.getFlowId());
                    edge.setFromNodeId(sourceNode.getId());
                    edge.setToNodeId(targetNode.getId());
                    edges.add(edge);
                }
            }
            if (!edges.isEmpty()) {
                // 连线不为空时批量保存
                flowEdgeRepository.saveAll(edges);
            } else {
                // 连线为空时不执行保存
            }
        } else {
            // 连线为空时不执行保存
        }
    }

    /**
     * 加载流程画布
     *
     * @param flowId 流程ID
     * @return 画布结果
     */
    public FlowCanvasResult loadCanvas(Long flowId) {
        // 第一步：校验流程ID
        if (flowId == null) {
            // 流程ID为空时抛出业务异常
            throw new BizException("流程ID不能为空");
        }
        // 第二步：校验流程是否存在
        Flow flow = flowRepository.findById(flowId).orElse(null);
        if (flow == null) {
            // 流程不存在时抛出业务异常
            throw new BizException("流程不存在");
        }
        // 第三步：查询节点与连线
        FlowCanvasResult result = new FlowCanvasResult();
        result.setNodes(flowNodeRepository.findByFlowId(flowId));
        result.setEdges(flowEdgeRepository.findByFlowId(flowId));
        return result;
    }

}
