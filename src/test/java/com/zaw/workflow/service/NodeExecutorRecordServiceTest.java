package com.zaw.workflow.service;

import com.zaw.workflow.entity.FlowEdge;
import com.zaw.workflow.entity.FlowNode;
import com.zaw.workflow.entity.NodeExecutorRecord;
import com.zaw.workflow.repository.FlowEdgeRepository;
import com.zaw.workflow.repository.FlowNodeRepository;
import com.zaw.workflow.repository.NodeExecutorRecordRepository;
import com.zaw.workflow.web.HumanInfoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NodeExecutorRecordServiceTest {

    @Mock
    private NodeExecutorRecordRepository nodeExecutorRecordRepository;

    @Mock
    private FlowNodeRepository flowNodeRepository;

    @Mock
    private FlowEdgeRepository flowEdgeRepository;

    @Test
    void getHumanInfo_buildsFormSchemaAndNextNodes() {
        NodeExecutorRecord record = new NodeExecutorRecord();
        record.setId(1L);
        record.setFlowInstanceId(10L);
        record.setNodeId(100L);

        FlowNode current = new FlowNode();
        current.setId(100L);
        current.setFlowId(5L);
        current.setNodeKey("node-1");
        current.setNodeName("Node 1");
        current.setConfig("{\"formSchema\":[{\"name\":\"fieldA\"},{\"name\":\"fieldB\"}]}");

        FlowNode next = new FlowNode();
        next.setId(200L);
        next.setFlowId(5L);
        next.setNodeKey("node-2");
        next.setNodeName("Node 2");

        FlowEdge edge = new FlowEdge();
        edge.setFromNodeId(100L);
        edge.setToNodeId(200L);
        edge.setFlowId(5L);

        when(nodeExecutorRecordRepository.findById(1L)).thenReturn(Optional.of(record));
        when(flowNodeRepository.findById(100L)).thenReturn(Optional.of(current));
        when(flowEdgeRepository.findByFlowId(5L)).thenReturn(List.of(edge));
        when(flowNodeRepository.findByFlowId(5L)).thenReturn(List.of(current, next));

        NodeExecutorRecordService service = new NodeExecutorRecordService(
                nodeExecutorRecordRepository,
                flowNodeRepository,
                flowEdgeRepository
        );

        HumanInfoResponse response = service.getHumanInfo(1L);

        assertThat(response.getFlowInstanceId()).isEqualTo(10L);
        assertThat(response.getNodeId()).isEqualTo(100L);
        assertThat(response.getFormSchema()).containsExactly("fieldA", "fieldB");
        assertThat(response.getOptions()).hasSize(1);
        assertThat(response.getOptions().get(0).getNodeKey()).isEqualTo("node-2");
        assertThat(response.getOptions().get(0).getLabel()).isEqualTo("Node 2");
    }
}
