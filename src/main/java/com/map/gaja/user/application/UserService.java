package com.map.gaja.user.application;

import com.map.gaja.global.authentication.AuthenticationHandler;
import com.map.gaja.global.authentication.SessionHandler;
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
    private final SessionHandler sessionHandler;

    @Transactional
    public Long login(LoginRequest request) {
        String email = oauth2Client.getEmail(request.getAccessToken());
        if (email == null) { //카카오 로그인 실패
            throw new UserNotFoundException();
        }

        sessionHandler.deduplicate(email); //중복로그인 처리 최대 2개까지

        User user = userRepository.findByEmail(email)
                .orElse(User.builder()
                        .email(email)
                        .authority(Authority.FREE)
                        .groupCount(0)
                        .lastLoginDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                        .build());
        userRepository.save(user);

        authenticationHandler.saveContext(email, user.getAuthority().toString()); //SecurityContextHolder에 인증 객체 저장

        //그룹 아이디로 응답해주면 클라이언트 쪽에서 그룹을 가지고 고객조회 API를 호출한다. null이면 호출X
        return user.getReferenceGroupId();
    }
}
