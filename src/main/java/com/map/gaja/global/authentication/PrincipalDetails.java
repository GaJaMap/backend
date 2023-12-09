package com.map.gaja.global.authentication;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class PrincipalDetails extends SessionDetails implements UserDetails, OAuth2User {
    private final String email;
    private final String authority;
    private final Map<String, Object> attributes;
    private static final String PLATFORM_TYPE_APP = "APP";
    private static final String PLATFORM_TYPE_WEB = "WEB";

    public PrincipalDetails(Long userId, String email, String authority) {
        super(userId, PLATFORM_TYPE_APP);
        this.email = email;
        this.authority = authority;
        this.attributes = null;
    }

    public PrincipalDetails(Long userId, String email, String authority, Map<String, Object> attributes) {
        super(userId, PLATFORM_TYPE_WEB);
        this.email = email;
        this.authority = authority;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public String getName() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> role = new ArrayList<>();

        role.add((GrantedAuthority) () -> authority);

        return role;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
