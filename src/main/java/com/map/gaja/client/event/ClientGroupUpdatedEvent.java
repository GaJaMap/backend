package com.map.gaja.client.event;

import com.map.gaja.user.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClientGroupUpdatedEvent {
    private final long previousGroupId;
    private final long changedGroupId;
    private final User owner;
}
