package com.zaw.aicode.dto;


import lombok.Data;

import java.util.List;

/**
 * @author lizhong.zhang@yituishui.com
 * @since 2026/1/2
 */
@Data
public class AskRequest {

    private String sessionId;

    private List<MessageRequest> messages;

}
