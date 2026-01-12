package com.zaw.workflow.repository;

import com.zaw.workflow.entity.FlowInstance;
import com.zaw.workflow.enums.FlowInstanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FlowInstanceRepository extends JpaRepository<FlowInstance, Long> {

    List<FlowInstance> findByFlowId(Long flowId);

    Optional<FlowInstance> findTopByFlowIdOrderByCreateDateDesc(Long flowId);

    List<FlowInstance> findByStatus(FlowInstanceStatus status);

    List<FlowInstance> findByBusinessKey(String businessKey);

    void deleteByFlowId(Long flowId);
}
