package com.map.gaja.client.event;

import com.map.gaja.user.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupClientAddedEvent {
    private final long groupId;
    private final User user;
}
