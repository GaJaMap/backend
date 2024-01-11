package com.map.gaja.group.event;

import com.map.gaja.user.domain.model.User;
import lombok.Getter;

@Getter
public class GroupDeletedEvent {
    private final User user;

    public GroupDeletedEvent(User user) {
        this.user = user;
    }
}
