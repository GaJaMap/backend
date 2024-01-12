package com.map.gaja.user.application;

import com.map.gaja.global.authentication.AuthenticationRepository;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.oauth2.KakaoEmailProvider;
import com.map.gaja.user.infrastructure.UserRepository;
import com.map.gaja.user.presentation.dto.request.LoginRequest;
import com.map.gaja.user.presentation.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.map.gaja.user.application.UserServiceHelper.loginByEmail;
import static com.map.gaja.user.application.UserServiceHelper.findById;
import static com.map.gaja.user.constant.UserConstant.APP_LOGIN;

@Service
@RequiredArgsConstructor
public class UserService {
    private final KakaoEmailProvider kakaoEmailProvider;
    private final UserRepository userRepository;
    private final AuthenticationRepository authenticationRepository;

    public LoginResponse login(LoginRequest request) {
        String email = kakaoEmailProvider.getEmail(request.getAccessToken());

        User user = loginByEmail(userRepository, email, APP_LOGIN);

        authenticationRepository.saveContext(user);

        return new LoginResponse(email,
                user.getAuthority().name(),
                user.getFormattedDateAsString());
    }

    @Transactional
    public void withdrawal(Long userId) {
        User user = findById(userRepository, userId);

        user.withdrawal();
    }
}