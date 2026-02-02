package com.zaw.workflow.web;

import lombok.Data;

@Data
public class UpdateFlowContextRequest {
    private Long id;

    private String contextConfig;
}
