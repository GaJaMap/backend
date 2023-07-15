package com.map.gaja.group.application;

import com.map.gaja.client.presentation.dto.subdto.GroupInfoDto;
import com.map.gaja.group.domain.exception.GroupNotFoundException;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.infrastructure.GroupQueryRepository;
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

import java.util.List;

import static com.map.gaja.user.application.UserServiceHelper.findExistingUser;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final GroupQueryRepository groupQueryRepository;

    @Transactional
    public void create(String email, GroupCreateRequest request) {
        User user = findExistingUser(userRepository, email);

        user.checkCreateGroupPermission();

        groupRepository.save(createGroup(request.getName(), user));

        user.increaseGroupCount();
    }

    private Group createGroup(String name, User user) {
        return new Group(name, user);
    }

    @Transactional(readOnly = true)
    public GroupResponse findGroups(String email, Pageable pageable) {
        User user = findExistingUser(userRepository, email);

        Slice<GroupInfo> groupInfos = groupRepository.findGroupByUserId(user.getId(), pageable);

        return new GroupResponse(groupInfos.hasNext(), groupInfos.getContent());
    }

    /**
     * 삭제되지 않은 그룹 정보 반환 -> 엑셀 등록 시에 그룹 정보 출력을 위해 필요.
     * @param email 로그인 이메일
     * @return
     */
    @Transactional(readOnly = true)
    public List<GroupInfoDto> findActiveGroupInfo(String email) {
        return groupQueryRepository.findActiveGroupInfo(email);
    }

    @Transactional
    public void delete(String email, Long groupId) {
        User user = findExistingUser(userRepository, email);

        int deletedGroupCount = groupRepository.deleteByIdAndUserId(groupId, user.getId());

        if (deletedGroupCount == 0) { //삭제된 번들이 없다면 존재하지 않은 번들이거나 userId가 다른 번들일 가능성이 있음
            throw new GroupNotFoundException();
        }

        user.decreaseGroupCount();
    }

    @Transactional
    public void updateName(String email, Long groupId, GroupUpdateRequest request) {
        User user = findExistingUser(userRepository, email);

        Group group = groupRepository.findByIdAndUserId(groupId, user.getId())
                .orElseThrow(GroupNotFoundException::new);
        group.updateName(request.getName());
    }
}
