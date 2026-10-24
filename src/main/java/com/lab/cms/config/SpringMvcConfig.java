package com.lab.cms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.lab.cms.interceptor.LoginInterceptor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan({"com.lab.cms.controller", "com.lab.cms.aspect"})
public class SpringMvcConfig implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> c : converters) {
            if (c instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter jmc = (MappingJackson2HttpMessageConverter) c;
                jmc.setDefaultCharset(StandardCharsets.UTF_8);

                ObjectMapper om = jmc.getObjectMapper();
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                JavaTimeModule jtm = new JavaTimeModule();
                jtm.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(fmt));
                jtm.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(fmt));
                om.registerModule(jtm);
                om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            }
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login", "/login/**",
                        "/login.jsp",
                        "/register.jsp",
                        "/error",
                        "/*.html",
                        "/resources/**", "/static/**",
                        "/article/**",
                        "/category/**",
                        "/tag/**",
                        "/log/**",
                        "/sys/**"
                );
    }
}