package com.map.gaja.group.event;

import com.map.gaja.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
class GroupEventListenerTest {
    @Autowired
    ApplicationEventPublisher publisher;

    @MockBean
    GroupEventListener groupEventListener;

    @Test
    @DisplayName("그룹이 생성되면 이벤트 리스너가 실행된다.")
    void groupCreateEvent() {
        //given
        GroupCreatedEvent groupCreatedEvent = new GroupCreatedEvent(new User("test"));

        //when
        publisher.publishEvent(groupCreatedEvent);

        //then
        verify(groupEventListener).create(groupCreatedEvent);
    }
}