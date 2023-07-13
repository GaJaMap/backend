package com.map.gaja.group.application;

import com.map.gaja.group.domain.exception.GroupNotFoundException;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.infrastructure.GroupQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupAccessVerifyService {
    private final GroupQueryRepository groupQueryRepository;

    public void verifyGroupAccess(long groupId, String userEmail) {
        Group group = groupQueryRepository.findGroupWithUser(groupId)
                .orElseThrow(GroupNotFoundException::new);

        if (isNotMatchingEmail(group, userEmail) || isDeleted(group)) {
            throw new GroupNotFoundException();
        }

        group.getUser().accessGroup(groupId);
    }

    private static boolean isNotMatchingEmail(Group group, String userEmail) {
        return !group.getUser().getEmail().equals(userEmail);
    }

    private static boolean isDeleted(Group group) {
        return group.getIsDeleted();
    }
}
