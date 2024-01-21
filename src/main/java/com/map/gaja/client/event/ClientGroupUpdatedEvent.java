package com.map.gaja.client.event;

import com.map.gaja.group.domain.model.Group;
import com.map.gaja.user.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClientGroupUpdatedEvent {
    private final Group previousGroup;
    private final Group changedGroup;
    private final User owner;
}
