package com.zaw.workflow.repository;

import com.zaw.workflow.entity.NodeExecutorRecord;
import com.zaw.workflow.enums.ExecutorStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NodeExecutorRecordRepository extends JpaRepository<NodeExecutorRecord, Long> {

    List<NodeExecutorRecord> findByFlowInstanceIdOrderByIdAsc(Long flowInstanceId);

    List<NodeExecutorRecord> findByFlowInstanceIdAndStatusOrderByIdAsc(Long flowInstanceId, ExecutorStatus status);

    void deleteByFlowInstanceId(Long flowInstanceId);

    void deleteByFlowInstanceIdIn(List<Long> flowInstanceIds);
}
