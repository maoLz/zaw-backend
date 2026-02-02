package com.zaw.workflow.engine;

import com.zaw.workflow.entity.*;
import com.zaw.workflow.enums.ActionType;
import com.zaw.workflow.enums.ExecutorStatus;
import com.zaw.workflow.enums.ExecutorType;
import com.zaw.workflow.enums.FlowInstanceStatus;
import com.zaw.workflow.enums.NodeType;
import com.zaw.workflow.dto.FlowExecContext;
import com.zaw.workflow.engine.config.HttpNodeConfig;
import com.zaw.workflow.engine.config.NodeConfig;
import com.zaw.workflow.engine.exec.NodeExecutor;
import com.zaw.workflow.engine.exec.NodeExecutorRegistry;
import com.zaw.workflow.engine.strategy.*;
import com.zaw.workflow.engine.util.JsonExtractor;
import com.zaw.workflow.repository.*;

import cn.hutool.core.collection.CollectionUtil;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.m;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlowEngineImpl {

    private final FlowInstanceRepository flowInstanceRepository;
    private final NodeExecutorRecordRepository nodeExecutorRecordRepository;
    private final NodeExecutorRegistry nodeExecutorRegistry;
    private final NodeExecutionStrategyFactory nodeExecutionStrategyFactory;

    @Async("asyncExecutor")
    public void run(Long instanceId, FlowNode current, FlowExecContext context,
            NodeExecutionStrategy strategy) {
        FlowInstance instance = flowInstanceRepository.findById(instanceId).orElse(null);
        context.setFlowInstance(instance);
        instance.setFlowNodeNumber(context.getNodes().size());
        while (true) {
            instance.setRunNodeNumber(instance.getRunNodeNumber()+1);
            NodeExecutorRecord record = null;
            if (instance.getStatus() == FlowInstanceStatus.WAITING
                    || instance.getStatus() == FlowInstanceStatus.FAILED) {
                record = context.getCurrentRecord();
                instance.setStatus(FlowInstanceStatus.RUNNING);
            } else {
                record = createRecord(instance, current);
            }
            context.setCurrentRecord(record);

            String output = "";
            try {
                log.info("CURRENT NODE KEY:" + current.getNodeKey());
                if (current.getNodeType() == NodeType.TASK) {
                    NodeExecutor executor = nodeExecutorRegistry.getRequired(current.getExecutorType());
                    output = executor.execute(context);
                    if (StringUtils.isNotBlank(output)) {
                        mergeContext(instance, current, output);
                    }
                } else {
                    record.setStatus(ExecutorStatus.SUCCESS);
                }
            } catch (Exception e) {
                log.error("ERROR CURRENT NODE KEY:" + current.getNodeKey());
                record.setStatus(ExecutorStatus.FAILED);
                record.setErrorMessage(e.getMessage());
                record.setEndTime(new Date());
                log.error(e.getMessage(), e);
                failInstance(instance, e.getMessage());
                return;
            } finally {
                nodeExecutorRecordRepository.save(record);
            }

            if (current.getNodeType() == NodeType.END) {
                instance.setStatus(FlowInstanceStatus.SUCCESS);
                instance.setEndTime(new Date());
                flowInstanceRepository.save(instance);
                return;
            }
            List<FlowEdge> edges = context.getEdges();
            Map<Long, FlowNode> nodeByKey = context.getNodeMap();
            if (current.getExecutorType() == ExecutorType.HUMAN) {
                if (record.getStatus() == ExecutorStatus.WAITING) {
                    instance.setStatus(FlowInstanceStatus.WAITING);
                    flowInstanceRepository.save(instance);
                    return;
                } else if (record.getStatus() == ExecutorStatus.SUCCESS) {
                    if (record.getAction() == ActionType.END) {
                        instance.setStatus(FlowInstanceStatus.SUCCESS);
                        instance.setEndTime(new Date());
                        flowInstanceRepository.save(instance);
                        return;
                    } else if (record.getAction() == ActionType.GOTO) {
                        current = context.getNodeMap().get(record.getNextNodeId());
                        current = pickNextFromMultiple(current, edges, nodeByKey, record.getNodeKey());
                        context.setCurrentNode(current);
                        continue;
                    }
                }
            }
            // 获取后续节点
            FlowNode next = pickNext(current, edges, nodeByKey);
            context.setCurrentNode(next);
            if (next == null) {
                instance.setStatus(FlowInstanceStatus.SUCCESS);
                instance.setEndTime(new Date());
                flowInstanceRepository.save(instance);
                return;
            }
            current = next;
        }
    }

    public static FlowNode pickNext(FlowNode current, List<FlowEdge> edges, Map<Long, FlowNode> nodeByKey) {
        return edges.stream()
                .filter(e -> Objects.equals(e.getFromNodeId(), current.getId()))
                .map(e -> nodeByKey.get(e.getToNodeId()))
                .findFirst()
                .orElse(null);
    }

    public static FlowNode pickNextFromMultiple(FlowNode current, List<FlowEdge> edges, Map<Long, FlowNode> nodeByKey,
            String gotoNodeKey) {
        return edges.stream()
                .filter(e -> Objects.equals(e.getFromNodeId(), current.getId()))
                .map(e -> nodeByKey.get(e.getToNodeId()))
                .filter(n -> Objects.equals(n.getNodeKey(), gotoNodeKey))
                .findFirst().orElseThrow(() -> new RuntimeException("goto node not found"));
    }

    private NodeExecutorRecord createRecord(FlowInstance instance, FlowNode node) {
        NodeExecutorRecord record = new NodeExecutorRecord();
        record.setFlowInstanceId(instance.getId());
        record.setNodeId(node.getId());
        record.setNodeKey(node.getNodeKey());
        record.setStatus(ExecutorStatus.RUNNING);
        record.setStartTime(new Date());
        record.setRetryCount(0);
        return nodeExecutorRecordRepository.save(record);
    }

    private void failInstance(FlowInstance instance, String message) {
        instance.setStatus(FlowInstanceStatus.FAILED);
        instance.setEndTime(new Date());
        instance.setErrorMessage(message);
        log.error(message);
        flowInstanceRepository.save(instance);
    }

    private void mergeContext(
            FlowInstance instance,
            FlowNode node,
            String outputJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // 1. 原 context
            JsonNode root = instance.getContext() == null
                    ? mapper.createObjectNode()
                    : mapper.readTree(instance.getContext());
            ObjectNode context = (ObjectNode) root;
            
            ObjectNode output = StringUtils.isBlank(instance.getOutput())?
                mapper.createObjectNode(): (ObjectNode) mapper.readTree(instance.getOutput());

            JsonNode outputNode = mapper.readTree(outputJson);

            // 3. outputMapping（可选）
            if (node.getConfig() != null) {
                NodeConfig config = JSONObject.parseObject(node.getConfig(), NodeConfig.class);
                Map<String, String> outputMapping = config.getOutputMapping();
                if (CollectionUtil.isNotEmpty(outputMapping)) {
                    outputMapping.entrySet().forEach(e->{
                        JsonNode v = JsonExtractor.extract(outputNode, e.getValue());
                        if (!v.isMissingNode()) {
                            context.set(e.getKey(), v);
                        }
                    });
                }
                Map<String,String> flowOutput = config.getFlowOutput();
                if(CollectionUtil.isNotEmpty(flowOutput)){
                    flowOutput.entrySet().forEach(e->{
                        JsonNode v = JsonExtractor.extract(outputNode, e.getValue());
                        if (!v.isMissingNode()) {
                            output.set(e.getKey(),v);
                        }
                    });
                }

                JsonNode cfg = mapper.readTree(node.getConfig());

                if (cfg.has("formSchema")) {
                    JsonNode formSchema = cfg.get("formSchema");
                    formSchema.elements().forEachRemaining(e -> {
                        if (e.has("contextPath")) {
                            JsonNode v = JsonExtractor.extract(outputNode, e.get("name").asText());
                            if (!v.isMissingNode()) {
                                context.set(e.get("contextPath").asText(), v);
                            }
                        }
                    });
                }
            }
       

            instance.setContext(mapper.writeValueAsString(context));
            instance.setOutput(mapper.writeValueAsString(output));

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to merge node output into context", e);
        }
    }
}
