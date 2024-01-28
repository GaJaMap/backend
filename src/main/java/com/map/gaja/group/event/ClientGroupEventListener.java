package com.map.gaja.group.event;

import com.map.gaja.client.event.GroupClientAddedEvent;
import com.map.gaja.client.event.ClientGroupUpdatedEvent;
import com.map.gaja.group.domain.service.IncreasingClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClientGroupEventListener {
    private final IncreasingClientService increasingClientService;
    private final int SINGLE_CLIENT = 1;

    @EventListener(GroupClientAddedEvent.class)
    public void increaseClientCount(GroupClientAddedEvent event) {
        increasingClientService.increaseByOne(event.getGroup(), event.getUser().getAuthority());
    }

    @EventListener(ClientGroupUpdatedEvent.class)
    public void changeGroup(ClientGroupUpdatedEvent event) {
        event.getPreviousGroup().decreaseClientCount(SINGLE_CLIENT);
        increasingClientService.increaseByOne(event.getChangedGroup(), event.getOwner().getAuthority());
    }
}
