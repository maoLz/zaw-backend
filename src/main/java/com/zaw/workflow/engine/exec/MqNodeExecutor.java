package com.zaw.workflow.engine.exec;

import com.zaw.workflow.entity.FlowNode;
import com.zaw.workflow.dto.FlowExecContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;


/**
 * MQ 执行器（占位）：
 * 真正实现通常需要：
 * - 发送消息到 MQ（Kafka/RabbitMQ 等）
 * - 或者订阅回调/事件驱动推进
 *
 * 本最简版：直接返回一个提示输出。
 */
@Component
public class MqNodeExecutor implements NodeExecutor {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String execute(FlowExecContext context) throws Exception {
        FlowNode node = context.getCurrentNode();

        return mapper.createObjectNode()
                .put("note", "MQ node is a stub in minimal engine. Implement producer + (optional) async resume.")
                .put("nodeKey", node.getNodeKey())
                .toString();
    }
}
