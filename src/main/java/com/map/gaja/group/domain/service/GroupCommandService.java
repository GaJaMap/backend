package com.map.gaja.group.domain.service;

import com.map.gaja.group.domain.exception.GroupNotFoundException;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.user.domain.model.User;
import org.springframework.stereotype.Service;

@Service
public class GroupCommandService {

    public Group create(String groupName, User user) {
        user.checkCreateGroupPermission();

        return new Group(groupName, user);
    }

    public void delete(GroupRepository groupRepository, Long userId, Long groupId) {
        int deletedGroupCount = groupRepository.deleteByIdAndUserId(groupId, userId);

        if (isGroupMissing(deletedGroupCount)) {
            throw new GroupNotFoundException();
        }
    }

    private boolean isGroupMissing(int deletedGroupCount) {
        return deletedGroupCount == 0;
    }
}
