package com.lss.config;

import com.lss.handler.FilterInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器拦截路径配置类
 *
 * @author lss
 * @create 2022年03月08日 13:33
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public FilterInterceptor getInterceptor() {
        return new FilterInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getInterceptor()).addPathPatterns("/**").excludePathPatterns("/**/login", "/files/**");
    }
}
