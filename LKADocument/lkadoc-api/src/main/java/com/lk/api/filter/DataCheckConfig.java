package com.lk.api.filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class DataCheckConfig implements WebMvcConfigurer {
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册TestInterceptor拦截器
        InterceptorRegistration registration = registry.addInterceptor(new DataCheckInterceptor());
        registration.addPathPatterns("/**");
        registration.excludePathPatterns("/**/*.html","/**/*.js","/**/*.css","/**/*.woff",
        		"/**/*.jpg","/**/*.png","/**/*.gif","/**/*.ttf");
    }
}