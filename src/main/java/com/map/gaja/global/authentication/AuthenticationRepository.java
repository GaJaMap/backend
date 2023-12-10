package com.map.gaja.global.authentication;

import com.map.gaja.user.domain.model.Authority;
import com.map.gaja.user.domain.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 세션 정보를 핸들하는 컴포넌트.
 * <p>
 * 무조건 인증된 사용자가 있을 때 사용할 것.
 * 인증되지 않은 사용자가 있을 때는 NULL 반환으로 인한 NPE 위험이 있음.
 */
@Component
public class AuthenticationRepository {

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
            if (mappedAuthority != null)
                currentUserAuthorityList.add(mappedAuthority);
        }

        return currentUserAuthorityList;
    }

    /**
     * SecurityContextHolder에 사용자 인증 객체 저장 및 세션 생성
     */
    public void saveContext(User user) {
        UserDetails userDetails = new PrincipalDetails(user.getId(), user.getEmail(), user.getAuthority().toString());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
        SecurityContextHolder.setContext(context);
    }

    /**
     * 세션에서 이메일 추출
     */
    public String getEmail() {
        PrincipalDetails principalDetails = getCurrentUser();
        return principalDetails != null ? principalDetails.getName() : null;
    }

}
