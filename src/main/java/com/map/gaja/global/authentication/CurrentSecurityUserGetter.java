package com.map.gaja.global.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 현재 요청한 사용자의 세션 정보를 가져오는 컴포넌트.
 *
 * 무조건 인증된 사용자가 있을 때 사용할 것.
 * 인증되지 않은 사용자가 있을 때는 NULL 반환으로 인한 NPE 위험이 있음.
 */
@Component
public class CurrentSecurityUserGetter {

    /**
     * 현재 요청한 사용자의 세션 정보를 가져온다.
     */
    public PrincipalDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated())
            return null;

        return (PrincipalDetails) authentication.getPrincipal();
    }

}
