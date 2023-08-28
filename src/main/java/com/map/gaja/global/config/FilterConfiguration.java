package com.map.gaja.global.config;

import com.map.gaja.global.filter.CustomSessionFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfiguration {

    @Bean
    public FilterRegistrationBean<CustomSessionFilter> registration(CustomSessionFilter filter) {
        FilterRegistrationBean<CustomSessionFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setOrder(Integer.MIN_VALUE);
        registration.addUrlPatterns("/*");
        return registration;
    }

}
