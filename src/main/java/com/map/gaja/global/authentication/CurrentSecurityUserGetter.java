package com.map.gaja.global.authentication;

import com.map.gaja.user.domain.model.Authority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 현재 요청한 사용자의 세션 정보를 가져오는 컴포넌트.
 *
 * 무조건 인증된 사용자가 있을 때 사용할 것.
 * 인증되지 않은 사용자가 있을 때는 NULL 반환으로 인한 NPE 위험이 있음.
 */
@Component
public class CurrentSecurityUserGetter {

    private Map<String, Authority> authorityMap;

    @PostConstruct
    public void init() {
        authorityMap = new HashMap<>();
        for (Authority value : Authority.values()) {
            authorityMap.put(value.name(), value);
        }
    }

    /**
     * 현재 요청한 사용자의 세션 정보를 가져온다.
     */
    public PrincipalDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated())
            return null;

        return (PrincipalDetails) authentication.getPrincipal();
    }

    /**
     * 현재 요청한 사용자의 세션 정보를 바탕으로 권한 정보를 가져온다.
     */
    public List<Authority> getAuthority() {
        List<Authority> currentUserAuthorityList = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : getCurrentUser().getAuthorities()) {
            Authority mappedAuthority = authorityMap.get(grantedAuthority.getAuthority());
            if(mappedAuthority != null)
                currentUserAuthorityList.add(mappedAuthority);
        }

        return currentUserAuthorityList;
    }

}
