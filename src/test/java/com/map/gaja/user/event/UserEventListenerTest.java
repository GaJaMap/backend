package com.map.gaja.user.event;

import com.map.gaja.global.event.Events;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.verify;

@SpringBootTest
class UserEventListenerTest {
    @Autowired
    ApplicationEventPublisher publisher;

    @MockBean
    UserEventListener userEventListener;

    @Test
    @DisplayName("로그인에 성공하면 이벤트 리스너가 실행된다.")
    void loginSucceededEvent() {
        // given
        LoginSucceededEvent loginSucceededEvent = new LoginSucceededEvent("email", "APP");

        // when
        Events.raise(loginSucceededEvent);

        // then
        verify(userEventListener).login(loginSucceededEvent);
    }

}