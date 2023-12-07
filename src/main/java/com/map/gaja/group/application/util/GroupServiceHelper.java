package com.map.gaja.group.application.util;

import com.map.gaja.group.domain.exception.GroupNotFoundException;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.infrastructure.GroupRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupServiceHelper {
    /**
     * ClientCount 등등 동시성 문제가 있는 필드들이 여러 곳에서 수정되는 것을 막기 위해
     * 생성, 수정, 삭제 시 사용할 조회
     */
    public static Group findGroupByIdForUpdating(GroupRepository groupRepository, long groupId) {
        return groupRepository.findGroupByIdForUpdate(groupId)
                .orElseThrow(() -> new GroupNotFoundException());
    }
}
