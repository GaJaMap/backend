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

@Slf4j
@Aspect
@RequiredArgsConstructor
@Component
public class ImageAuthCheckingAspect {

    private final CurrentSecurityUserGetter userGetter;
    private final List<ImageUploadRequestChecker> requestCheckers;

    @Before("@within(com.map.gaja.global.authentication.imageuploads.ImageAuthChecking) " +
            "|| @annotation(com.map.gaja.global.authentication.imageuploads.ImageAuthChecking)")
    public void checkAuthority(JoinPoint jp) throws Exception {
        PrincipalDetails detail = userGetter.getCurrentUser();
        if (isFreeAuth(detail) && isImageUploadingRequest(jp.getArgs())) {
            throw new ImageUploadPermissionException(Authority.FREE.toString());
        }
    }

    private boolean isImageUploadingRequest(Object[] args) {
        for (ImageUploadRequestChecker requestChecker : requestCheckers) {
            if(requestChecker.isSupported(args))
                return requestChecker.isImageUploadingRequest(args);
        }

        return false;
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
