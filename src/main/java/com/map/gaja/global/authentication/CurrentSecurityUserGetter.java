package com.map.gaja.global.authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentSecurityUserGetter {
    public PrincipalDetails getCurrentUser() {
        return (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
