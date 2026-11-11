package com.lab.cms.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
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

                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                JavaTimeModule jtm = new JavaTimeModule();
                jtm.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(fmt));
                jtm.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(fmt));

                // 注意：不能直接 jmc.getObjectMapper().registerModule(jtm)。
                // 框架默认的 ObjectMapper 通过 Jackson2ObjectMapperBuilder 构建，已经自动注册过
                // JavaTimeModule（并自带 ISO 序列化器），再注册一个同类型模块不会覆盖先注册的规则，
                // 结果时间仍然输出成 2026-11-27T10:28:08 这种带 T 的 ISO 格式。
                // 这里换成一个自己完全掌控的 ObjectMapper，序列化规则才确定生效。
                ObjectMapper om = new ObjectMapper();
                om.registerModule(jtm);
                om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                // 联调阶段前端可能多传字段，不因未知属性直接报错
                om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                jmc.setObjectMapper(om);
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