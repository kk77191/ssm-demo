package com.lab.cms.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public Result<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常: {}, 请求: {}", e.getMessage(), request.getRequestURI());
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler(PermissionException.class)
    @ResponseBody
    public Result<Void> handlePermissionException(PermissionException e, HttpServletRequest request) {
        log.warn("权限异常: {}, 请求: {}", e.getMessage(), request.getRequestURI());
        Result<Void> r = Result.fail(e.getMessage());
        r.setCode(403);
        return r;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseBody
    public Result<Void> handleValidationException(Exception e, HttpServletRequest request) {
        String msg;
        if (e instanceof MethodArgumentNotValidException) {
            msg = ((MethodArgumentNotValidException) e).getBindingResult().getFieldErrors()
                    .stream().map(FieldError::getDefaultMessage).collect(Collectors.joining("; "));
        } else {
            msg = ((BindException) e).getBindingResult().getFieldErrors()
                    .stream().map(FieldError::getDefaultMessage).collect(Collectors.joining("; "));
        }
        log.warn("参数校验失败: {}, 请求: {}", msg, request.getRequestURI());
        return Result.fail("参数校验失败: " + msg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public Result<Void> handleConstraintViolation(ConstraintViolationException e, HttpServletRequest request) {
        String msg = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage).collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}, 请求: {}", msg, request.getRequestURI());
        return Result.fail("参数校验失败: " + msg);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常, 请求: {}", request.getRequestURI(), e);
        return Result.fail("系统繁忙, 请稍后再试");
    }
}