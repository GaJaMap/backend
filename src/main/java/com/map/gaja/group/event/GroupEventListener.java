package com.map.gaja.group.event;

import com.map.gaja.user.domain.model.User;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class GroupEventListener {

    @EventListener(GroupCreatedEvent.class)
    public void create(GroupCreatedEvent event) {
        User user = event.getUser();
        user.increaseGroupCount();
    }

}
