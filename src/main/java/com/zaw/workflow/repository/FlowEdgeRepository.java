package com.zaw.workflow.repository;

import com.zaw.workflow.entity.FlowEdge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FlowEdgeRepository extends JpaRepository<FlowEdge, Long> {
    List<FlowEdge> findByFlowId(Long flowId);

    Optional<FlowEdge> findByFlowIdAndToNodeId(Long flowId, Long toNodeId);

    void deleteByFlowId(Long flowId);
}
