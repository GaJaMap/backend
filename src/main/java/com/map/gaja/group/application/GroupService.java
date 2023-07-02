package com.map.gaja.group.application;

import com.map.gaja.group.domain.exception.GroupNotFoundException;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.group.presentation.dto.request.GroupCreateRequest;
import com.map.gaja.group.presentation.dto.request.GroupUpdateRequest;
import com.map.gaja.group.presentation.dto.response.GroupInfo;
import com.map.gaja.group.presentation.dto.response.GroupResponse;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.map.gaja.user.application.UserServiceHelper.findExistingUser;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    @Transactional
    public void create(String email, GroupCreateRequest request) {
        User user = findExistingUser(userRepository, email);

        user.checkCreateGroupPermission();

        groupRepository.save(createGroup(request.getName(), user));

        user.increaseGroupCount();
    }

    private Group createGroup(String name, User user) {
        return Group.builder()
                .name(name)
                .clientCount(0)
                .user(user)
                .build();
    }

    @Transactional(readOnly = true)
    public GroupResponse findGroups(String email, Pageable pageable) {
        User user = findExistingUser(userRepository, email);

        Slice<GroupInfo> groupInfos = groupRepository.findGroupByUserId(user.getId(), pageable);

        return new GroupResponse(groupInfos.hasNext(), groupInfos.getContent());
    }

    @Transactional
    public void delete(String email, Long groupId) {
        User user = findExistingUser(userRepository, email);

        int deletedBundleCount = groupRepository.deleteByIdAndUserId(groupId, user.getId());

        if (deletedBundleCount == 0) { //삭제된 번들이 없다면 존재하지 않은 번들이거나 userId가 다른 번들일 가능성이 있음
            throw new GroupNotFoundException();
        }

        clientRepository.deleteByGroupId(groupId);
    }

    @Transactional
    public void updateName(String email, GroupUpdateRequest request) {
        User user = findExistingUser(userRepository, email);

        Group group = groupRepository.findByIdAndUserId(request.getGroupId(), user.getId())
                .orElseThrow(GroupNotFoundException::new);
        group.updateName(request.getName());
    }
}
