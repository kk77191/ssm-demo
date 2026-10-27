package com.lab.cms.config;

import com.lab.cms.filter.CorsFilter;
import com.lab.cms.filter.LoginFilter;
import com.lab.cms.filter.XssFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
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
        return new Filter[]{encoding};
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