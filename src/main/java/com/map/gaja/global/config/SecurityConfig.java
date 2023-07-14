package com.map.gaja.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(request -> request
                        .antMatchers("/api/**").authenticated()
                        .antMatchers("/").permitAll()) // 엑셀 페이지 설정
                .csrf().disable()
                .formLogin().disable();

        return http.build();
    }
}