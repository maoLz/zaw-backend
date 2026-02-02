package com.zaw.workflow.repository;

import com.zaw.workflow.entity.NodeExecutorRecord;
import com.zaw.workflow.enums.ExecutorStatus;
import com.zaw.workflow.web.NodeExecutorRecordSimpleResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NodeExecutorRecordRepository extends JpaRepository<NodeExecutorRecord, Long> {

    List<NodeExecutorRecord> findByFlowInstanceIdOrderByIdAsc(Long flowInstanceId);

    List<NodeExecutorRecord> findByFlowInstanceIdAndStatusOrderByIdAsc(Long flowInstanceId, ExecutorStatus status);

    @Query("""
            select new com.zaw.workflow.web.NodeExecutorRecordSimpleResponse(r.id,r.status, r.nodeKey, r.startTime)
            from NodeExecutorRecord r
            where r.flowInstanceId = :flowInstanceId
            order by r.id asc
            """)
    List<NodeExecutorRecordSimpleResponse> findSimpleByFlowInstanceIdOrderByIdAsc(
            @Param("flowInstanceId") Long flowInstanceId
    );

    void deleteByFlowInstanceId(Long flowInstanceId);

    void deleteByFlowInstanceIdIn(List<Long> flowInstanceIds);
}
