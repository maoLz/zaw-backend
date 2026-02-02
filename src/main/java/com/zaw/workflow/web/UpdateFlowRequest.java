package com.zaw.workflow.web;

import com.zaw.workflow.enums.FlowStatus;
import lombok.Data;

@Data
public class UpdateFlowRequest {
    private String name;

    private String code;

    private String description;

    private String contextConfig;

    private Integer flowVersion;

    private FlowStatus status;
}
