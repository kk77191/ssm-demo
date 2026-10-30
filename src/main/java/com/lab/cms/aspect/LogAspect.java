package com.lab.cms.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab.cms.annotation.Log;
import com.lab.cms.entity.SysOperationLog;
import com.lab.cms.entity.SysUser;
import com.lab.cms.mapper.SysOperationLogMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;

@Aspect
@Component
public class LogAspect {

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    @Autowired
    private SysOperationLogMapper operationLogMapper;

    private final ObjectMapper om = new ObjectMapper();

    @Around("@annotation(com.lab.cms.annotation.Log)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long start = System.currentTimeMillis();
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        Log logAnno = method.getAnnotation(Log.class);
        String operation = logAnno.value().isEmpty() ? method.getName() : logAnno.value();

        log.info("[LogAspect] 拦截到操作: {}", operation);

        SysOperationLog opLog = new SysOperationLog();
        opLog.setOperation(operation);
        opLog.setMethod(point.getTarget().getClass().getName() + "." + method.getName());

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            opLog.setIp(getIp(request));
            try {
                opLog.setParams(om.writeValueAsString(point.getArgs()));
            } catch (Exception e) {
                opLog.setParams("[" + point.getArgs().length + "个参数,序列化失败:" + e.getMessage() + "]");
            }
            HttpSession session = request.getSession(false);
            if (session != null) {
                SysUser user = (SysUser) session.getAttribute("loginUser");
                if (user != null) {
                    opLog.setUserId(user.getId());
                    opLog.setUsername(user.getUsername());
                }
            }
        }

        Object result;
        try {
            result = point.proceed();
        } catch (Throwable t) {
            opLog.setOperation(operation + "(异常)");
            throw t;
        } finally {
            try {
                int rows = operationLogMapper.insert(opLog);
                log.info("[操作日志] 保存成功, id={}, user={}, op={}", opLog.getId(), opLog.getUsername(), opLog.getOperation());
            } catch (Exception e) {
                log.error("[操作日志] 保存失败! {} | {}", e.getClass().getSimpleName(), e.getMessage());
            }
            log.info("[{}] 操作耗时: {}ms", operation, System.currentTimeMillis() - start);
        }
        return result;
    }

    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}