package com.map.gaja.global.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

public class LimitedPageableArgumentResolver extends PageableHandlerMethodArgumentResolver {

    @Override
    public Pageable resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String pageSize = webRequest.getParameter(getParameterNameToUse(getSizeParameterName(), methodParameter));

        if (pageSize != null) {
            throw new IllegalArgumentException("페이지 사이즈 크기를 지정할 수 없습니다.");
        }

        return super.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
    }
}
