package com.zaw.aicode.dto;

import lombok.Data;

import java.util.List;
@Data
public class LogProbs {

    private List<TokenLogProb> content;
    private List<TokenLogProb> reasoningContent;

    public static class TokenLogProb {
        private String token;
        private Double logprob;
        private List<Integer> bytes;
        private List<TokenLogProb> topLogprobs;
    }
}
