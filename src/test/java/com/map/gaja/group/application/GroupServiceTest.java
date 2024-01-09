package com.map.gaja.group.application;

import com.map.gaja.group.domain.exception.GroupNotFoundException;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.domain.service.GroupCommandService;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.group.presentation.dto.request.GroupCreateRequest;
import com.map.gaja.group.presentation.dto.request.GroupUpdateRequest;

import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {
    @Mock
    GroupRepository groupRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    GroupCommandService groupCommandService;

    @InjectMocks
    GroupService groupService;

    @Test
    @DisplayName("그룹 생성 성공")
    void createGroupSuccess() {
        // given
        GroupCreateRequest groupCreateRequest = new GroupCreateRequest("group");
        User user = new User("email");
        Group group = Group.builder()
                .id(1L)
                .build();

        given(userRepository.findByEmailAndActiveForUpdate(anyLong()))
                .willReturn(Optional.of(user));
        given(groupCommandService.create(anyString(), any(User.class)))
                .willReturn(group);

        // when
        Long groupId = groupService.create(1L, groupCreateRequest);

        // then
        verify(groupRepository).save(any(Group.class));
        assertThat(groupId).isEqualTo(1L);
    }

    @Test
    @DisplayName("그룹명 변경 실패")
    void updateGroupNameFail() {
        // given
        User user = new User("email");
        GroupUpdateRequest request = new GroupUpdateRequest("test");

        given(groupRepository.findByIdAndUserId(anyLong(), anyLong()))
                .willReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> {
            groupService.updateName(1L, 1L, request);
        }).isInstanceOf(GroupNotFoundException.class);
    }

    @Test
    @DisplayName("그룹명 변경 성공")
    void updateGroupNameSuccess() {
        // given
        User user = new User("test");
        GroupUpdateRequest request = new GroupUpdateRequest("update");
        Group group = Group.builder()
                .name("group")
                .build();

        given(groupRepository.findByIdAndUserId(anyLong(), anyLong()))
                .willReturn(Optional.of(group));

        // when
        groupService.updateName(1L, 1L, request);

        // then
        assertThat(group.getName()).isEqualTo(request.getName());
    }
}