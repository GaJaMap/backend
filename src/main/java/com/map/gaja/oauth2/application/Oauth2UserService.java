package com.map.gaja.oauth2.application;

import com.map.gaja.global.authentication.PrincipalDetails;
import com.map.gaja.global.authentication.SessionHandler;
import com.map.gaja.user.domain.exception.WithdrawalUserException;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.map.gaja.user.application.UserServiceHelper.findByEmail;

@Service
@RequiredArgsConstructor
public class Oauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final SessionHandler sessionHandler;
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        Map<String, Object> attributes = parseAttributes(userRequest);

        String email = (String) ((Map) attributes.get("kakao_account")).get("email");

        User user;
        try {
            user = findByEmail(userRepository, email);
        } catch (WithdrawalUserException e) { //회원 탈퇴한 유저일 경우
            throw new OAuth2AuthenticationException(""); //Oauth2AuthenticationException으로 변환해줘야지 Oauth2FailureHandler가 예외를 잡을 수 있다.
        }

        sessionHandler.deduplicate(email, "WEB"); //중복 세션 제거

        return new PrincipalDetails(user.getEmail(), user.getAuthority().name(), attributes);

    }

    private Map<String, Object> parseAttributes(OAuth2UserRequest userRequest) {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes(); // OAuth 서비스의 유저 정보들
        return attributes;
    }

}
