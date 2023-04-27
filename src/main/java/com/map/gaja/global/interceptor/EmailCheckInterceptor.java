package com.map.gaja.global.interceptor;

import com.map.gaja.global.resolver.LoginEmailResolver;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 테스트를 위한 인터셉터이기 떄문에 언제든 삭제
 */
public class EmailCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 파라미터로 값이 들어오는 간단한 상황 가정
        String email = (String) request.getParameter("email");


        // test로 들어오는 상황만 정상처리
        if (email != null && email.equals("test")) {
            request.setAttribute(LoginEmailResolver.LOGIN_EMAIL_ATTRIBUTE, email); // 컨트롤러로 가기 전에 리졸버에서 이 값을 까봄
            return true;
        }

        throw new Exception();
    }
}
