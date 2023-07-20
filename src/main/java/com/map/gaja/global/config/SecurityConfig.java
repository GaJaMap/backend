package com.map.gaja.global.config;

import com.map.gaja.oauth2.application.Oauth2UserService;
import com.map.gaja.oauth2.handler.Oauth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final Oauth2UserService oauth2UserService;
    private final Oauth2SuccessHandler oauth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .authorizeHttpRequests(request -> request
                        .antMatchers("/api/user/login",
                                "/", "/login", "/css/**", "/js/**", "/image/**", "/file/**" // 웹 페이지 설정
                        ).permitAll()
                        .anyRequest().authenticated())
                .csrf().disable()
                .formLogin().disable()
                .oauth2Login()
                    .userInfoEndpoint().userService(oauth2UserService) //oauth2 로그인 후 처리 로직
                .and()
                .successHandler(oauth2SuccessHandler) //웹에서 oauth2 로그인 성공할 경우
                .and()
                .exceptionHandling()
                    .authenticationEntryPoint(new Http403ForbiddenEntryPoint()); //인증되지 없을 경우 로그인 창으로 이동하는데 앱에서는 403으로 응답해주기 위해서 403으로 통일함

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web
                .ignoring()
                .antMatchers(getPathInSwagger());
    }

    private String[] getPathInSwagger() {
        return new String[]{
                "/swagger",
                "/swagger-ui/index.html",
                "/swagger-ui/swagger-ui.css",
                "/swagger-ui/index.css",
                "/swagger-ui/swagger-ui-bundle.js",
                "/swagger-ui/swagger-ui-standalone-preset.js",
                "/swagger-ui/swagger-initializer.js",
                "/v3/api-docs/swagger-config",
                "/swagger-ui/favicon-32x32.png",
                "/v3/api-docs/user-api",
                "/v3/api-docs/group-api",
                "/v3/api-docs/client-api",
                "/favicon.ico"
        };
    }
}