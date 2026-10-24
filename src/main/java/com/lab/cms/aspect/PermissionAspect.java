package com.lab.cms.aspect;

import com.lab.cms.annotation.RequireRole;
import com.lab.cms.common.PermissionException;
import com.lab.cms.entity.SysUser;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;

@Aspect
@Component
public class PermissionAspect {

    @Around("@annotation(com.lab.cms.annotation.RequireRole) || @within(com.lab.cms.annotation.RequireRole)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        RequireRole anno = AnnotationUtils.findAnnotation(method, RequireRole.class);
        if (anno == null) {
            anno = AnnotationUtils.findAnnotation(point.getTarget().getClass(), RequireRole.class);
        }
        if (anno == null) {
            return point.proceed();
        }

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new PermissionException("无法获取请求上下文");
        }
        HttpSession session = attrs.getRequest().getSession(false);
        if (session == null) {
            throw new PermissionException("请先登录");
        }
        SysUser user = (SysUser) session.getAttribute("loginUser");
        if (user == null) {
            throw new PermissionException("请先登录");
        }

        String[] roles = anno.value();
        boolean hasRole = false;
        for (String r : roles) {
            if (r.equalsIgnoreCase(user.getRole())) {
                hasRole = true;
                break;
            }
        }
        if (!hasRole) {
            throw new PermissionException("权限不足, 需要角色: " + String.join(", ", roles));
        }

        return point.proceed();
    }
}