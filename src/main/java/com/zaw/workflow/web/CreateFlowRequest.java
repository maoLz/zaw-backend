package com.zaw.workflow.web;

import lombok.*;

@Getter
@Setter
public class CreateFlowRequest {

    private String name;

    private String code;

    private String description;

    private String contextConfig;
}
