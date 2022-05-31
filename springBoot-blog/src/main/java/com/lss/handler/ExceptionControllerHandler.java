package com.lss.handler;

import com.lss.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 *
 * @author lss
 * @create 2022年05月31日 20:26
 */
@RestControllerAdvice
public class ExceptionControllerHandler {

    @ExceptionHandler(RuntimeException.class)
    public Result<?> runtime() {
        Result<Object> result = new Result<>();
        result.setCode(500);
        result.setMessage("操作失败");
        return result;
    }
}
