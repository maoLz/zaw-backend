package com.zaw.workflow.engine.config;


import lombok.Data;

import java.util.List;

/**
 * @author lizhong.zhang@yituishui.com
 * @since 2026/1/5
 */
@Data
public class HumanNodeConfig {

    private List<FormSchema> formSchemas;

    @Data
    private static class FormSchema {
        private String name;

        private String contextPath;
    }
}
