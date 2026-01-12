package com.zaw.workflow.entity;

import com.zaw.common.entity.BaseModel;
import com.zaw.workflow.enums.FlowInstanceStatus;
import com.zaw.workflow.enums.FlowOperationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "flow_instance")
@Getter
@Setter
public class FlowInstance extends BaseModel {

    @Column(nullable = false)
    private Long flowId;

    private Integer flowVersion;

    private String businessKey;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private FlowInstanceStatus status;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private FlowOperationType flowOperationType;

    private Date startTime;

    private Date endTime;

    @Column(columnDefinition = "json")
    private String context;

    @Column(columnDefinition = "json")
    private String input;

    @Column(columnDefinition = "text")
    private String output;


}
