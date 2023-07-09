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
        Group group = groupQueryRepository.findGroupByUser(groupId, userEmail)
                .orElseThrow(GroupNotFoundException::new);

        group.getUser().accessGroup(groupId);
    }
}
