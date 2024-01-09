package com.map.gaja.user.application;

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
import static com.map.gaja.user.constant.OAuthConstant.*;

@Service
@RequiredArgsConstructor
public class OAuth2WebService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final SessionHandler sessionHandler;
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        Map<String, Object> attributes = parseAttributes(userRequest); //oauth 사용자 정보 map으로 파싱

        String email = extractEmail(attributes);

        User user = getUser(email);

        sessionHandler.deduplicate(email, WEB_LOGIN);

        return new PrincipalDetails(user.getId(), user.getEmail(), user.getAuthority().name(), attributes);

    }

    private User getUser(String email) {
        try {
            return findByEmail(userRepository, email);
        } catch (WithdrawalUserException e) { //회원 탈퇴한 유저일 경우
            throw new OAuth2AuthenticationException(FAIL_STATUS_STRING); //OAuth2AuthenticationException으로 변환해야지 OAuth2WebFailureHandler가 예외를 잡을 수 있다.
        }
    }

    private String extractEmail(Map<String, Object> attributes) {
        return (String) ((Map) attributes.get(KAKAO_ACCOUNT)).get(EMAIL);
    }

    private Map<String, Object> parseAttributes(OAuth2UserRequest userRequest) {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes(); // OAuth 서비스의 유저 정보들
        return attributes;
    }

}
