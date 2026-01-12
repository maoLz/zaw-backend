package com.zaw.workflow.web;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HumanInfoResponse {

    private Long flowInstanceId;

    private Long nodeId;

    private List<String> formSchema;

    private List<NextNode> options;

    @Getter
    @Setter
    public static class NextNode {

        private String nodeKey;

        private String label;
    }
}
