package com.map.gaja.client.apllication.aop;

import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.global.authentication.CurrentSecurityUserGetter;
import com.map.gaja.global.authentication.PrincipalDetails;
import com.map.gaja.user.domain.model.Authority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Slf4j
@Aspect
@RequiredArgsConstructor
@Component
public class ClientImageAuthCheckingAspect {

    private final CurrentSecurityUserGetter userGetter;

    @Before("@within(com.map.gaja.client.apllication.aop.ClientImageAuth) " +
            "|| @annotation(com.map.gaja.client.apllication.aop.ClientImageAuth)")
    public void checkAuthority(JoinPoint jp) throws Exception {
        NewClientRequest clientRequest = getClientRequestArgs(jp.getArgs());
        if (clientRequest == null) {
            return;
        }

        PrincipalDetails detail = userGetter.getCurrentUser();
        System.out.println(isFreeAuth(detail));
        System.out.println(isImageUpdateRequest(clientRequest));
        if (isFreeAuth(detail) && isImageUpdateRequest(clientRequest)) {
            throw new RuntimeException("커스텀 예외 필요");
        }
    }

    private NewClientRequest getClientRequestArgs(Object[] args) {
        NewClientRequest clientRequest = null;
        for (Object arg : args) {
            if (arg instanceof NewClientRequest) {
                clientRequest = (NewClientRequest) arg;
                break;
            }
        }
        return clientRequest;
    }

    private boolean isImageUpdateRequest(NewClientRequest clientRequest) {
        return clientRequest != null && !clientRequest.getIsBasicImage();
    }

    private boolean isFreeAuth(PrincipalDetails detail) {
        Iterator<? extends GrantedAuthority> iterator = detail.getAuthorities().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getAuthority().equals(Authority.FREE.toString())) {
                return true;
            }
        }

        return false;
    }

}
