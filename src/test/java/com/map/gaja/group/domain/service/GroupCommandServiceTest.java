package com.map.gaja.group.domain.service;

import com.map.gaja.group.domain.exception.GroupNotFoundException;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.user.domain.exception.GroupLimitExceededException;
import com.map.gaja.user.domain.model.Authority;
import com.map.gaja.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupCommandServiceTest {
    @Mock
    GroupRepository groupRepository;

    GroupCommandService groupCommandService = new GroupCommandService();

    @Test
    @DisplayName("등급 제한으로 그룹 생성 실패")
    void createGroupFail() {
        String email = "test@gmail.com";
        User user = User.builder()
                .id(1L)
                .email(email)
                .groupCount(100)
                .authority(Authority.FREE)
                .lastLoginDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();

        assertThatThrownBy(() -> groupCommandService.create("group", user)).isInstanceOf(GroupLimitExceededException.class);
    }

    @Test
    @DisplayName("그룹이 존재하지 않아 그룹 삭제 실패")
    void deleteGroupFail() {
        when(groupRepository.deleteByIdAndUserId(anyLong(),anyLong())).thenReturn(0);

        assertThatThrownBy(() -> groupCommandService.delete(groupRepository, anyLong(), anyLong()))
                .isInstanceOf(GroupNotFoundException.class);
    }
}