package com.zaw.workflow.entity;

import com.zaw.common.entity.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "flow_edge")
@Getter
@Setter
public class FlowEdge extends BaseModel {

    @Column(nullable = false)
    private Long flowId;

    private Long fromNodeId;

    private Long toNodeId;

    private String conditionExpression;
}
