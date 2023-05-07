package com.map.gaja.bundle.application;

import com.map.gaja.bundle.infrastructure.BundleRepository;
import com.map.gaja.bundle.presentation.dto.request.BundleCreateRequest;
import com.map.gaja.user.domain.exception.BundleLimitExceededException;
import com.map.gaja.user.domain.model.Authority;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BundleServiceTest {
    @Mock
    BundleRepository bundleRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    BundleService bundleService;

    @Test
    void 번들_생성_실패() {
        String email="test@gmail.com";
        BundleCreateRequest bundleCreateRequest = new BundleCreateRequest("bundle");
        User user = User.builder()
                .id(1L)
                .email(email)
                .bundleCount(100)
                .authority(Authority.FREE)
                .createdDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .lastLoginDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThatThrownBy(()->bundleService.create(email, bundleCreateRequest)).isInstanceOf(BundleLimitExceededException.class);
    }

}