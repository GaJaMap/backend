package com.map.gaja.group.domain.service;

import com.map.gaja.group.domain.exception.GroupNotFoundException;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.event.GroupCreatedEvent;
import com.map.gaja.group.event.GroupDeletedEvent;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupCommandService {
    private final ApplicationEventPublisher publisher;

    public Group create(String groupName, User user) {
        user.checkCreateGroupPermission();

        publisher.publishEvent(new GroupCreatedEvent(user));

        return new Group(groupName, user);
    }

    public void delete(GroupRepository groupRepository, User user, Long groupId) {
        int deletedGroupCount = groupRepository.deleteByIdAndUserId(groupId, user.getId());

        if (isGroupMissing(deletedGroupCount)) {
            throw new GroupNotFoundException();
        }

        publisher.publishEvent(new GroupDeletedEvent(user));
    }

    private boolean isGroupMissing(int deletedGroupCount) {
        return deletedGroupCount == 0;
    }
}
