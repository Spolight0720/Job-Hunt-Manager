package com.jobhunt.exception;

import com.jobhunt.common.Result;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理我们自定义的防重复投递异常
     */
    @ExceptionHandler(RepeatApplicationException.class)
    public Result<Void> handleRepeatApplicationException(RepeatApplicationException e) {
        return Result.error(409, e.getMessage());
    }

    /**
     * 处理 @Validated 参数校验错误异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder errorMessage = new StringBuilder("参数校验失败: ");
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMessage.append(fieldError.getDefaultMessage()).append("; ");
        }
        return Result.error(400, errorMessage.toString());
    }

    /**
     * 处理其他业务抛出的 RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        return Result.error(400, e.getMessage());
    }

    /**
     * 处理兜底的服务器异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        e.printStackTrace();
        return Result.error(500, "服务器内部错误，请联系管理员");
    }
}
