package com.map.gaja.user.application;

import com.map.gaja.fixture.UserFixture;
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
        // given
        User user = UserFixture.createWithdrawnUser();
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(user);

        // when, then
        assertThatThrownBy(() -> userServiceHelper.loginByEmail(userRepository, user.getEmail(), "APP"));

    }

    @Test
    @DisplayName("신규 유저가 로그인할 경우")
    void newUserLogin() {
        // given
        String email = "test@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(null);

        // when
        UserServiceHelper.loginByEmail(userRepository, email, "APP");

        // then
        verify(userRepository, times(1)).save(any());
    }

}