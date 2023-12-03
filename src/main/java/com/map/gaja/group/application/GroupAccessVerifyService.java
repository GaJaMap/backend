package com.map.gaja.group.application;

import com.map.gaja.group.domain.exception.GroupNotFoundException;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.domain.service.IncreasingClientService;
import com.map.gaja.group.infrastructure.GroupQueryRepository;
import com.map.gaja.user.domain.model.Authority;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupAccessVerifyService {
    private final GroupQueryRepository groupQueryRepository;
    private final IncreasingClientService increasingClientService;

    public void verifyGroupAccess(long groupId, String userEmail) {
        Group group = groupQueryRepository.findGroupWithUser(groupId)
                .orElseThrow(GroupNotFoundException::new);

        if (isNotMatchingEmail(group, userEmail) || isDeleted(group)) {
            throw new GroupNotFoundException();
        }

        group.getUser().accessGroup(groupId);
    }

    /**
     * Excel 파싱후 Insert 진행 중,
     * 카카오 API 호출 전에 해당 함수를 호출해서 API 호출 수를 줄이기 위해 만들었다.
     * verifyGroupAccess + 현재 insertCount의 수 만큼 Group에 넣을 수 있는지
     */
    public void verifyClientInsertAccess(long groupId, String userEmail, int insertCount) {
        Group group = groupQueryRepository.findGroupWithUser(groupId)
                .orElseThrow(GroupNotFoundException::new);
        Authority userAuth = group.getUser().getAuthority();

        if (isNotMatchingEmail(group, userEmail) || isDeleted(group)) {
            throw new GroupNotFoundException();
        }

        increasingClientService.checkCanCreateClient(userAuth, group.getClientCount(), insertCount);

        group.getUser().accessGroup(groupId);
    }

    private static boolean isNotMatchingEmail(Group group, String userEmail) {
        return !group.getUser().getEmail().equals(userEmail);
    }

    private static boolean isDeleted(Group group) {
        return group.getIsDeleted();
    }
}
