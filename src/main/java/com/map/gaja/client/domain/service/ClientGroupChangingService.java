package com.map.gaja.client.domain.service;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.event.ClientGroupUpdatedEvent;
import com.map.gaja.global.event.Events;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.user.domain.model.User;
import org.springframework.stereotype.Service;

@Service
public class ClientGroupChangingService {

    public void changeGroup(Client client, Group changedGroup, User user) {
        if(client.getGroup() != changedGroup)
            return;

        Events.raise(new ClientGroupUpdatedEvent(client.getGroup(), changedGroup, user));
        client.updateGroup(changedGroup);
    }

}
