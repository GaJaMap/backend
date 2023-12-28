package com.map.gaja.group.application;

import com.map.gaja.group.domain.exception.GroupNotFoundException;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.domain.service.GroupCommandService;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.group.presentation.dto.request.GroupCreateRequest;
import com.map.gaja.group.presentation.dto.request.GroupUpdateRequest;
import com.map.gaja.user.domain.model.Authority;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
        String email = "test@gmail.com";
        GroupCreateRequest groupCreateRequest = new GroupCreateRequest("group");
        User user = new User(email);

        when(userRepository.findByEmailAndActiveForUpdate(user.getId())).thenReturn(Optional.of(user));
        when(groupCommandService.create(any(), any())).thenReturn(new Group("group", user));

        groupService.create(user.getId(), groupCreateRequest);

        verify(groupRepository, times(1)).save(any());
        assertEquals(1, user.getGroupCount());
    }

    @Test
    @DisplayName("그룹 삭제 성공")
    void deleteGroupSuccess() {
        String email = "test@gmail.com";
        User user = User.builder()
                .id(1L)
                .email(email)
                .groupCount(1)
                .active(true)
                .authority(Authority.FREE)
                .lastLoginDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();

        when(userRepository.findByEmailAndActiveForUpdate(user.getId())).thenReturn(Optional.of(user));

        groupService.delete(user.getId(), 1L);

        assertEquals(0, user.getGroupCount());

    }

    @Test
    @DisplayName("그룹명 변경 실패")
    void updateGroupNameFail() {
        String email = "test@gmail.com";
        User user = User.builder()
                .id(1L)
                .email(email)
                .groupCount(0)
                .active(true)
                .authority(Authority.FREE)
                .lastLoginDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();
        GroupUpdateRequest request = new GroupUpdateRequest("test");

        when(groupRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> {
            groupService.updateName(user.getId(), 1L, request);
        })
                .isInstanceOf(GroupNotFoundException.class);
    }

    @Test
    @DisplayName("그룹명 변경 성공")
    void updateGroupNameSuccess() {
        String email = "test@gmail.com";
        User user = User.builder()
                .id(1L)
                .email(email)
                .groupCount(0)
                .active(true)
                .authority(Authority.FREE)
                .lastLoginDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();
        GroupUpdateRequest request = new GroupUpdateRequest("test");
        Group group = new Group("name", user);

        when(groupRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(group));

        groupService.updateName(user.getId(), 1L, request);
        assertEquals(request.getName(), group.getName());
    }
}