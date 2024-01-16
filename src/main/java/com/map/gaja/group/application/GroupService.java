package com.map.gaja.group.application;

import com.map.gaja.client.presentation.dto.subdto.GroupDetailDto;
import com.map.gaja.group.domain.exception.GroupNotFoundException;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.domain.service.GroupCommandService;
import com.map.gaja.group.infrastructure.GroupQueryRepository;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.group.presentation.dto.request.GroupCreateRequest;
import com.map.gaja.group.presentation.dto.request.GroupUpdateRequest;
import com.map.gaja.group.presentation.dto.response.GroupInfo;
import com.map.gaja.group.presentation.dto.response.GroupResponse;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.map.gaja.user.application.UserServiceHelper.*;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupQueryRepository groupQueryRepository;
    private final GroupCommandService groupCommandService;

    @Transactional
    public Long create(Long userId, GroupCreateRequest request) {
        User user = findByEmailAndActiveWithLock(userRepository, userId);

        Group group = groupCommandService.create(request.getName(), user);
        groupRepository.save(group);

        return group.getId();
    }

    @Transactional(readOnly = true)
    public GroupResponse findGroups(Long userId, Pageable pageable) {
        Slice<GroupInfo> groupInfos = groupRepository.findGroupByUserId(userId, pageable);

        return new GroupResponse(groupInfos.hasNext(), groupInfos.getContent());
    }

    /**
     * 삭제되지 않은 그룹 정보 반환 -> 엑셀 등록 시에 그룹 정보 출력을 위해 필요.
     *
     * @param email 로그인 이메일
     * @return
     */
    @Transactional(readOnly = true)
    public List<GroupDetailDto> findActiveGroupInfo(String email) {
        return groupQueryRepository.findActiveGroupInfo(email);
    }

    @Transactional
    public void delete(Long userId, Long groupId) {
        User user = findByEmailAndActiveWithLock(userRepository, userId);

        groupCommandService.delete(groupRepository, user, groupId);
    }


    @Transactional
    public void updateName(Long userId, Long groupId, GroupUpdateRequest request) {
        Group group = groupRepository.findByIdAndUserId(groupId, userId)
                .orElseThrow(GroupNotFoundException::new);
        group.updateName(request.getName());
    }

}
