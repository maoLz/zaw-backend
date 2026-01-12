package com.zaw.common.exception;

/**
 * 业务异常
 */
public class BizException extends RuntimeException {

    /**
     * 构造业务异常
     *
     * @param message 异常信息
     */
    public BizException(String message) {
        super(message);
    }
}
