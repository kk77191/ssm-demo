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

        if (uri.equals(ctx + "/login.jsp") || uri.startsWith(ctx + "/login")) {
            chain.doFilter(req, res);
            return;
        }

        if (uri.startsWith(ctx + "/article") || uri.startsWith(ctx + "/category")
                || uri.startsWith(ctx + "/tag") || uri.startsWith(ctx + "/log")
                || uri.startsWith(ctx + "/sys")) {
            chain.doFilter(req, res);
            return;
        }

        Object user = request.getSession().getAttribute("loginUser");
        if (user != null) {
            chain.doFilter(req, res);
            return;
        }

        if (uri.endsWith(".jsp")) {
            response.sendRedirect(ctx + "/login.jsp");
            return;
        }

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }
}