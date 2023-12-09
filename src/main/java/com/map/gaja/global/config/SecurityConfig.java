package com.map.gaja.global.config;

import com.map.gaja.global.filter.RemoveAnonymousAuthenticationFilter;
import com.map.gaja.user.infrastructure.oauth2.OAuth2WebService;
import com.map.gaja.user.infrastructure.oauth2.handler.OAuth2WebFailureHandler;
import com.map.gaja.user.infrastructure.oauth2.handler.OAuth2WebSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2WebService OAuth2WebService;
    private final OAuth2WebSuccessHandler OAuth2WebSuccessHandler;
    private final OAuth2WebFailureHandler OAuth2WebFailureHandler;

    @Value("${management.endpoints.web.base-path}")
    private String monitoringEndPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String monitoringPath = monitoringEndPoint + "/**";

        http
                .httpBasic().disable()
                .authorizeHttpRequests(request -> request
                        .antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger").hasAuthority("ADMIN")
                        .antMatchers("/api/user/login",
                                monitoringPath,
                                "/policy/**", "/", "/login", "/css/**", "/js/**", "/image/**", "/file/**" // 웹 페이지 설정
                        ).permitAll()
                        .anyRequest().authenticated())
                .csrf().disable()
                .addFilterBefore(new RemoveAnonymousAuthenticationFilter(), AnonymousAuthenticationFilter.class)
                .formLogin().disable()
                .oauth2Login()
                .userInfoEndpoint().userService(OAuth2WebService) //oauth2 로그인 후 처리 로직
                .and()
                .successHandler(OAuth2WebSuccessHandler) //웹에서 oauth2 로그인 성공할 경우
                .failureHandler(OAuth2WebFailureHandler) //웹에서 oauth2 로그인 실패할 경우(회원탈퇴 유저가 로그인하면 예외)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)); //인증되지 없을 경우 로그인 창으로 이동하는데 앱에서는 401으로 응답해주기 위해서 401으로 통일함


        return http.build();
    }

}