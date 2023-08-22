package com.map.gaja.user.application;

import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceHelperTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    private UserServiceHelper userServiceHelper;

    @Test
    @DisplayName("회원 탈퇴한 유저가 로그인할 경우")
    void withdrawalUserLogin() {
        String email = "test@gmail.com";
        User user = User.builder()
                .email(email)
                .active(false)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(user);

        assertThatThrownBy(()->userServiceHelper.findByEmail(userRepository, email));

    }


}