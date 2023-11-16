package com.map.gaja.user.application;

import com.map.gaja.global.authentication.AuthenticationRepository;
import com.map.gaja.global.authentication.SessionHandler;
import com.map.gaja.user.domain.exception.UserNotFoundException;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.Oauth2Client;
import com.map.gaja.user.infrastructure.UserRepository;
import com.map.gaja.user.presentation.dto.request.LoginRequest;
import com.map.gaja.user.presentation.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

import static com.map.gaja.user.application.UserServiceHelper.findByEmail;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Oauth2Client oauth2Client;
    private final UserRepository userRepository;
    private final AuthenticationRepository authenticationRepository;
    private final SessionHandler sessionHandler;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String email = oauth2Client.getEmail(request.getAccessToken());
        if (email == null) { //카카오 로그인 실패
            throw new UserNotFoundException();
        }

        User user = findByEmail(userRepository, email);

        sessionHandler.deduplicate(email, "APP"); //중복로그인 처리 최대 2개까지

        authenticationRepository.saveContext(user.getId(), email, user.getAuthority().toString()); //SecurityContextHolder에 인증 객체 저장

        return new LoginResponse(email,
                user.getAuthority().name(),
                user.getLastLoginDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        );
    }

    @Transactional
    public void withdrawal(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    throw new UserNotFoundException();
                });

        sessionHandler.deleteAllByEmail(user.getEmail());

        user.withdrawal();
    }
}