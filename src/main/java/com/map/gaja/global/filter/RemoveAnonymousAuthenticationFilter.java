package com.map.gaja.global.filter;

import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class RemoveAnonymousAuthenticationFilter extends OncePerRequestFilter {
    private static final String SPRING_SECURITY_SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(request, response);

        HttpSession session = request.getSession(false);
        if (isAnonymousAuthentication(session)) {
            session.invalidate();
        }

    }

    private boolean isAnonymousAuthentication(HttpSession session) {
        if (session == null) {
            return false;
        }

        //익명 객체는 세션의 속성 키 값이 "SPRING_SECURITY_SAVED_REQUEST" 이고 인증 객체의 키 값은 "SPRING_SECURITY_CONTEXT"
        SavedRequest savedRequest = (SavedRequest) session.getAttribute(SPRING_SECURITY_SAVED_REQUEST);
        if (savedRequest != null) {
            return true;
        }
        return false;
    }

}
