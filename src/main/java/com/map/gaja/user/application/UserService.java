package com.map.gaja.user.application;

import com.map.gaja.global.authentication.AuthenticationHandler;
import com.map.gaja.user.domain.exception.UserNotFoundException;
import com.map.gaja.user.domain.model.Authority;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.Oauth2Client;
import com.map.gaja.user.infrastructure.UserRepository;
import com.map.gaja.user.presentation.dto.request.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Oauth2Client oauth2Client;
    private final UserRepository userRepository;
    private final AuthenticationHandler authenticationHandler;

    @Transactional
    public void login(LoginRequest request) {
        String email = oauth2Client.getEmail(request.getAccessToken());
        if (email == null) { //카카오 로그인 실패
            throw new UserNotFoundException();
        }

        User user = userRepository.findByEmail(email)
                .orElse(User.builder()
                        .email(email)
                        .authority(Authority.FREE)
                        .bundleCount(0)
                        .createdDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                        .lastLoginDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                        .build());

        authenticationHandler.saveContext(email, user.getAuthority().toString()); //SecurityContextHolder에 인증 객체 저장

        //기존 유저는 클라이언트 정보를 리턴해야함.
    }
}
