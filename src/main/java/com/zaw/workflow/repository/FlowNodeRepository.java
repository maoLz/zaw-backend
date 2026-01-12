package com.zaw.workflow.repository;

import com.zaw.workflow.entity.FlowNode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlowNodeRepository extends JpaRepository<FlowNode, Long> {
    List<FlowNode> findByFlowId(Long flowId);

    void deleteByFlowId(Long flowId);
}
