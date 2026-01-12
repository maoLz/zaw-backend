package com.zaw.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author huangzw 统一返回消息枚举
 * @date 2024/2/27
 */
@AllArgsConstructor
@Getter
public enum RE {
    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    SUCCESS_EXPORT(200, "正在导出中，请在文件下载中查看"),

    ;
    private int code;
    private String msg;


}
