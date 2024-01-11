package com.map.gaja.group.domain.service;

import com.map.gaja.group.domain.exception.GroupNotFoundException;
import com.map.gaja.group.event.GroupDeletedEvent;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.user.domain.exception.GroupLimitExceededException;
import com.map.gaja.user.domain.model.Authority;
import com.map.gaja.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.any;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GroupCommandServiceTest {
    @Mock
    GroupRepository groupRepository;

    @Mock
    ApplicationEventPublisher publisher;

    @InjectMocks
    GroupCommandService groupCommandService;

    @Test
    @DisplayName("등급 제한으로 그룹 생성 실패")
    void createGroupFail() {
        User user = User.builder()
                .groupCount(100)
                .authority(Authority.FREE)
                .lastLoginDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();

        assertThatThrownBy(() -> groupCommandService.create("group", user)).isInstanceOf(GroupLimitExceededException.class);
    }

    @Test
    @DisplayName("그룹이 존재하지 않아 그룹 삭제 실패")
    void deleteGroupFail() {
        // given
        User user = User.builder()
                .id(1L)
                .build();
        given(groupRepository.deleteByIdAndUserId(anyLong(), anyLong()))
                .willReturn(0);

        // when, then
        assertThatThrownBy(() -> groupCommandService.delete(groupRepository, user, 1L))
                .isInstanceOf(GroupNotFoundException.class);
    }

    @Test
    @DisplayName("그룹 삭제 성공")
    void deleteGroupSuccess() {
        // given
        User user = User.builder()
                .id(1L)
                .build();
        given(groupRepository.deleteByIdAndUserId(anyLong(), anyLong()))
                .willReturn(1);

        // when, then
        assertDoesNotThrow(()->groupCommandService.delete(groupRepository, user, 1L));
    }
}