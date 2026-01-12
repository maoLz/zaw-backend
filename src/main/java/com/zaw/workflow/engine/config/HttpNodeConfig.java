package com.zaw.workflow.engine.config;



import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * @author lizhong.zhang@yituishui.com
 * @since 2026/1/5
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class HttpNodeConfig extends NodeConfig{

    private String url;

    private String method;

    private List<HttpParam> params;

    private Map<String,String> headers;


    @Data
    public static class HttpParam {
        private String name;
        private JsonNode value;
        private String location;
        private String type;
    }

}
