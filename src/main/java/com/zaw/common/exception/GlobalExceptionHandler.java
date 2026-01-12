package com.zaw.common.exception;

import com.zaw.common.web.R;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.http.HttpStatus;

/**
 * 全局异常处理
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     *
     * @param ex 业务异常
     * @return 统一返回结果
     */
    @ExceptionHandler(BizException.class)
    public R<Void> handleBizException(BizException ex) {
        log.error(ex.getMessage(),ex);;
        return R.fail(ex.getMessage());
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNoHandler() {
    }



    /**
     * 处理未知异常
     *
     * @param ex 异常信息
     * @return 统一返回结果
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception ex) {
        log.error(ex.getMessage(),ex);;
        return R.fail("系统繁忙，请稍后重试");
    }
}
