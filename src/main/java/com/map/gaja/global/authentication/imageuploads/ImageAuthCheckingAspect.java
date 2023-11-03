package com.map.gaja.global.authentication.imageuploads;

import com.map.gaja.global.authentication.CurrentSecurityUserGetter;
import com.map.gaja.global.authentication.PrincipalDetails;
import com.map.gaja.global.authentication.imageuploads.checkers.ImageUploadRequestChecker;
import com.map.gaja.user.domain.exception.ImageUploadPermissionException;
import com.map.gaja.user.domain.model.Authority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

/**
 * ImageAuthChecking 어노테이션의 AOP 구현체
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
@Component
public class ImageAuthCheckingAspect {

    private final CurrentSecurityUserGetter userGetter;
    private final List<ImageUploadRequestChecker> requestCheckers;


    /**
     * 사용자가 Free 등급으로 이미지 업로드 요청을 한다면 ImageUploadPermissionException.class 를 발생시킴.
     * @param jp
     */
    @Before("@within(com.map.gaja.global.authentication.imageuploads.ImageAuthChecking) " +
            "|| @annotation(com.map.gaja.global.authentication.imageuploads.ImageAuthChecking)")
    public void checkAuthority(JoinPoint jp) {
        if (isFreeAuth() && isImageUploadingRequest(jp.getArgs())) {
            throw new ImageUploadPermissionException(Authority.FREE.toString());
        }
    }

    /**
     * @param args JoinPoint로 가져온 파라미터 Object 배열
     * @return 이미지 업로드 요청이 맞는가?
     */
    private boolean isImageUploadingRequest(Object[] args) {
        for (ImageUploadRequestChecker requestChecker : requestCheckers) {
            if(requestChecker.isSupported(args))
                return requestChecker.isImageUploadingRequest(args);
        }

        return false;
    }

    /**
     * @return Free 등급의 사용자인가?
     */
    private boolean isFreeAuth() {
        return userGetter.getAuthority().contains(Authority.FREE);
    }

}
