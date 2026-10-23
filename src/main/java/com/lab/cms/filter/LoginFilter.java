package com.lab.cms.filter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab.cms.common.Result;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String uri = request.getRequestURI();
        String ctx = request.getContextPath();

        if (uri.equals(ctx + "/login.jsp") || uri.startsWith(ctx + "/login/")) {
            chain.doFilter(req, res);
            return;
        }

        Object user = request.getSession().getAttribute("loginUser");
        if (user != null) {
            chain.doFilter(req, res);
            return;
        }

        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("text/html")) {
            response.sendRedirect(ctx + "/login.jsp");
            return;
        }

        response.setContentType("application/json;charset=utf-8");
        new ObjectMapper().writeValue(response.getWriter(), Result.fail("请登录"));
    }

    @Override
    public void destroy() {
    }
}