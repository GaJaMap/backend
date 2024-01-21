package com.map.gaja.client.event;

import com.map.gaja.group.domain.model.Group;
import com.map.gaja.user.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupClientAddedEvent {
    private final Group group;
    private final User user;
}
