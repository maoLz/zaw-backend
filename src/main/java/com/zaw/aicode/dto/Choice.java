package com.zaw.aicode.dto;

import lombok.Data;

@Data
public class Choice {

    private Integer index;
    private String finishReason;    // stop | length | tool_calls ...
    private Message message;
    private LogProbs logprobs;

}
