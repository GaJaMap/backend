package com.map.gaja.global.filter;

import com.map.gaja.global.resolver.LoginEmailResolver;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 테스트를 위한 필터이기 떄문에 언제든 삭제
 *
 */
public class EmailCheckFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 파라미터로 값이 들어오는 간단한 상황 가정
        String email = (String) request.getParameter("email");

        // test로 들어오는 상황만 정상처리
        if (email != null && email.equals("test")) {
            request.setAttribute(LoginEmailResolver.LOGIN_EMAIL_ATTRIBUTE, email); // 컨트롤러로 가기 전에 리졸버에서 이 값을 까봄
            filterChain.doFilter(request, response);
        }
        else {
            response.setCharacterEncoding("utf-8");
            response.getWriter().write("접근불가");
        }
    }
}
