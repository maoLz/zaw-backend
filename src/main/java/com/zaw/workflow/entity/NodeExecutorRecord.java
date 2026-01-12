package com.zaw.workflow.entity;

import com.zaw.common.entity.BaseModel;
import com.zaw.workflow.enums.ActionType;
import com.zaw.workflow.enums.ExecutorStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "node_executor_record")
@Getter
@Setter
public class NodeExecutorRecord extends BaseModel {

    @Column(nullable = false)
    private Long flowInstanceId;

    @Column(nullable = false)
    private Long nodeId;

    @Column(nullable = false)
    private String nodeKey;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private ExecutorStatus status;

    @Column(columnDefinition = "json")
    private String input;

    @Column(columnDefinition = "json")
    private String output;

    @Column(columnDefinition = "text")
    private String errorMessage;

    private Date startTime;

    private Date endTime;

    private Integer retryCount;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private ActionType action;

    private Long nextNodeId;

}
