package com.zaw.workflow.web;

import com.zaw.workflow.enums.FlowInstanceStatus;

import lombok.Data;

@Data
public class FlowInstanceResponse {

    private Long id;
    
    private String businessKey;

    private FlowInstanceStatus status;

    private String duration;

    private String startDateTime;

    private Long runNodeNumber;

    private Long flowNodeNumber;

    private String errorMessage;


}
