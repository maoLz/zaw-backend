package com.zaw.workflow.engine.config;


import lombok.Data;

import java.util.Map;

/**
 * @author lizhong.zhang@yituishui.com
 * @since 2026/1/5
 */
@Data
public class NodeConfig {


    private Map<String,String> outputMapping;

}
