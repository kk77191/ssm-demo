package com.lab.cms.interceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab.cms.common.Result;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object user = request.getSession().getAttribute("loginUser");
        if (user != null) {
            return true;
        }

        String accept = request.getHeader("Accept");
        String requestURI = request.getRequestURI();

        if (accept != null && accept.contains("text/html")) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return false;
        }

        response.setContentType("application/json;charset=utf-8");
        new ObjectMapper().writeValue(response.getWriter(), Result.fail("请登录"));
        return false;
    }
}