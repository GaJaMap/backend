package com.map.gaja.common;

import com.map.gaja.group.event.GroupEventListener;
import com.map.gaja.user.event.UserEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;

@SpringBootTest
public abstract class EventTest {
    @Autowired
    protected ApplicationEventPublisher publisher;

    @MockBean
    protected GroupEventListener groupEventListener;

    @MockBean
    protected UserEventListener userEventListener;
}
