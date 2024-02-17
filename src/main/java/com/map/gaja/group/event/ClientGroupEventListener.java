package com.map.gaja.group.event;

import com.map.gaja.client.event.GroupClientAddedEvent;
import com.map.gaja.client.event.ClientGroupUpdatedEvent;
import com.map.gaja.group.application.util.GroupServiceHelper;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.domain.service.IncreasingClientService;
import com.map.gaja.group.infrastructure.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientGroupEventListener {
    private final IncreasingClientService increasingClientService;
    private final GroupRepository groupRepository;
    private final int SINGLE_CLIENT = 1;

    @EventListener(GroupClientAddedEvent.class)
    public void increaseClientCount(GroupClientAddedEvent event) {
        Group group = GroupServiceHelper.findGroupByIdForUpdating(groupRepository, event.getGroupId());
        increasingClientService.increaseByOne(group, event.getUser().getAuthority());
    }

    @EventListener(ClientGroupUpdatedEvent.class)
    public void changeGroup(ClientGroupUpdatedEvent event) {
        // Row에 Lock을 걸기때문에 주의를 요함
        if(event.getChangedGroupId() == event.getPreviousGroupId())
            throw new IllegalArgumentException();

        Group previousGroup = GroupServiceHelper.findGroupByIdForUpdating(groupRepository, event.getPreviousGroupId());
        Group changedGroup = GroupServiceHelper.findGroupByIdForUpdating(groupRepository, event.getChangedGroupId());
        previousGroup.decreaseClientCount(SINGLE_CLIENT);
        increasingClientService.increaseByOne(changedGroup, event.getOwner().getAuthority());
    }
}
