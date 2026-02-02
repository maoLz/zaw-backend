package com.zaw.aicode.dto;


import lombok.Data;

/**
 * @author lizhong.zhang@yituishui.com
 * @since 2026/1/2
 */
@Data
public class MessageRequest {

    private Role role;

    private String content;

}
