package com.lab.cms.config;

import com.lab.cms.filter.CorsFilter;
import com.lab.cms.filter.LoginFilter;
import com.lab.cms.filter.XssFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.FormContentFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class ServletInitConfig extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{SpringConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{SpringMvcConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter encoding = new CharacterEncodingFilter();
        encoding.setEncoding("UTF-8");
        encoding.setForceEncoding(true);

        // FormContentFilter：Servlet 规范只对 POST 解析 application/x-www-form-urlencoded 请求体，
        // PUT / PATCH / DELETE 的请求体不会进入 parameter map，导致 @ModelAttribute 绑定出全 null 对象。
        // 这个过滤器会把 PUT/PATCH/DELETE 的表单体读出来塞进 parameter，前端用表单方式提交 PUT 才能被 Controller 接收。
        // 顺序：先编码、再解析表单体，两者不能颠倒。
        return new Filter[]{encoding, new FormContentFilter()};
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);

        FilterRegistration.Dynamic cors = servletContext.addFilter("corsFilter", new CorsFilter());
        cors.addMappingForUrlPatterns(null, true, "/*");
        cors.setAsyncSupported(true);

        FilterRegistration.Dynamic xss = servletContext.addFilter("xssFilter", new XssFilter());
        xss.addMappingForUrlPatterns(null, true, "/*");
        xss.setAsyncSupported(true);

        FilterRegistration.Dynamic loginFilter = servletContext.addFilter("loginFilter", new LoginFilter());
        loginFilter.addMappingForUrlPatterns(null, true, "/*");
        loginFilter.setAsyncSupported(true);
    }
}