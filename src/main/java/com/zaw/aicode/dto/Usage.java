package com.zaw.aicode.dto;

import lombok.Data;

@Data
public class Usage {

    private Integer promptTokens;
    private Integer completionTokens;
    private Integer totalTokens;
    private Integer promptCacheHitTokens;
    private Integer promptCacheMissTokens;
    private CompletionTokensDetails completionTokensDetails;

    public static class CompletionTokensDetails {
        private Integer reasoningTokens;
    }
}
