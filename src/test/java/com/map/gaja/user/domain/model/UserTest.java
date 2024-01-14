package com.map.gaja.user.domain.model;

import com.map.gaja.global.event.Events;
import com.map.gaja.user.event.AutoLoginSucceededEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserTest {
    @Mock
    ApplicationEventPublisher publisher;

    @InjectMocks
    Events events;

    @Test
    @DisplayName("날짜가 다르면 최근 접속일 update 이벤트를 발행한다.")
    void updateLastLoginDateSuccess() {
        // given
        LocalDateTime beforeDate = LocalDateTime.now().minus(2, ChronoUnit.DAYS);
        User user = User.builder()
                .id(1L)
                .lastLoginDate(beforeDate)
                .build();

        // when
        user.updateLastLoginDateIfDifferent();

        // then
        verify(publisher).publishEvent(any(AutoLoginSucceededEvent.class));
    }

    @Test
    @DisplayName("날짜가 같으면 최근 접속일 update 이벤트를 발행하지 않는다.")
    void updateLastLoginDateFail() {
        // given
        LocalDateTime beforeDate = LocalDateTime.now();
        User user = User.builder()
                .lastLoginDate(beforeDate)
                .build();

        // when
        user.updateLastLoginDateIfDifferent();

        // then
        verify(publisher, times(0)).publishEvent(any(AutoLoginSucceededEvent.class));
    }
}