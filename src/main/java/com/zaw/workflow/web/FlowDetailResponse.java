package com.zaw.workflow.web;

import com.zaw.workflow.enums.FlowStatus;

import lombok.Data;

@Data
public class FlowDetailResponse {

    private String name;

    private String code;

    private String description;

    private String params;

    private FlowStatus status;
}
