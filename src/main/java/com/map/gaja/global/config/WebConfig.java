package com.map.gaja.global.config;

import com.map.gaja.global.filter.EmailCheckFilter;
import com.map.gaja.global.resolver.LoginEmailResolver;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginEmailResolver());
    }

    @Bean
    public FilterRegistrationBean<Filter> testFilter() {
        FilterRegistrationBean<Filter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new EmailCheckFilter());
//        filterBean.setOrder(1);
        filterBean.addUrlPatterns("/api/test");
        return filterBean;
    }
}
