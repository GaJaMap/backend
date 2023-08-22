package com.map.gaja.oauth2.application;

import com.map.gaja.global.authentication.PrincipalDetails;
import com.map.gaja.global.authentication.SessionHandler;
import com.map.gaja.user.application.UserServiceHelper;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class Oauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final SessionHandler sessionHandler;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        Map<String, Object> attributes = parseAttributes(userRequest);

        String email = (String) ((Map) attributes.get("kakao_account")).get("email");

        User user = UserServiceHelper.findExistingUser(userRepository, email);
        userRepository.save(user);

        sessionHandler.deduplicate(email); //중복 세션 제거

        return new PrincipalDetails(user.getEmail(), user.getAuthority().name(), attributes);

    }

    private Map<String, Object> parseAttributes(OAuth2UserRequest userRequest) {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes(); // OAuth 서비스의 유저 정보들
        return attributes;
    }

}
