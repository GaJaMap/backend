package com.map.gaja.global.resolver;

import com.map.gaja.global.annotation.LoginEmail;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

public class LoginEmailResolver implements HandlerMethodArgumentResolver {
    public final static String LOGIN_EMAIL_ATTRIBUTE = "LOGIN_EMAIL";
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasLoginEmailAnnotation = parameter.hasParameterAnnotation(LoginEmail.class); // 이 어노테이션이 있니?
        boolean hasStringType = String.class.isAssignableFrom(parameter.getParameterType());// 파라미터가 String을 상속 or 구현 했니?
        return hasLoginEmailAnnotation && hasStringType; // 이 두 조건이 만족하면 아래 메서드 실행
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        return request.getAttribute(LOGIN_EMAIL_ATTRIBUTE);
    }
}
