package com.map.gaja.group.application;

import com.map.gaja.group.domain.exception.GroupNotFoundException;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.group.presentation.dto.request.GroupCreateRequest;
import com.map.gaja.group.presentation.dto.request.GroupUpdateRequest;
import com.map.gaja.user.domain.exception.GroupLimitExceededException;
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

    @InjectMocks
    GroupService groupService;

    @Test
    @DisplayName("등급 제한으로 그룹 생성 실패")
    void createGroupFail() {
        String email = "test@gmail.com";
        GroupCreateRequest groupCreateRequest = new GroupCreateRequest("bundle");
        User user = User.builder()
                .id(1L)
                .email(email)
                .groupCount(100)
                .authority(Authority.FREE)
                .lastLoginDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> groupService.create(email, groupCreateRequest)).isInstanceOf(GroupLimitExceededException.class);
    }

    @Test
    @DisplayName("그룹 생성 성공")
    void createGroupSuccess() {
        String email = "test@gmail.com";
        GroupCreateRequest groupCreateRequest = new GroupCreateRequest("group");
        User user = new User(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(groupRepository.save(any())).thenReturn(new Group("group", user));

        Long groupId = groupService.create(email, groupCreateRequest);

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

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(groupRepository.deleteByIdAndUserId(1L, 1L)).thenReturn(1);

        groupService.delete(email, 1L);

        assertEquals(0, user.getGroupCount());


    }

    @Test
    @DisplayName("그룹 삭제 실패")
    void deleteGroupFail() {
        String email = "test@gmail.com";
        User user = User.builder()
                .id(1L)
                .email(email)
                .groupCount(0)
                .active(true)
                .authority(Authority.FREE)
                .lastLoginDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(groupRepository.deleteByIdAndUserId(1L, 1L)).thenReturn(0);

        assertThatThrownBy(() -> {
            groupService.delete(email, 1L);
        })
                .isInstanceOf(GroupNotFoundException.class);

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

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(groupRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> {
            groupService.updateName(email, 1L, request);
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

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(groupRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(group));

        groupService.updateName(email, 1L, request);
        assertEquals(request.getName(), group.getName());
    }

    @Test
    @DisplayName("전체 그룹 조회")
    void findWholeGroup() {
        String email = "test@gmail.com";
        User user = new User(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertEquals(null, groupService.findGroup(email));
    }

}