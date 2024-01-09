package com.map.gaja.group.event;

import com.map.gaja.user.domain.model.User;
import lombok.Getter;

@Getter
public class GroupCreatedEvent {
    private final User user;

    public GroupCreatedEvent(User user) {
        this.user = user;
    }
}
