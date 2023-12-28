package com.map.gaja.group.domain.service;

import com.map.gaja.user.domain.exception.GroupLimitExceededException;
import com.map.gaja.user.domain.model.Authority;
import com.map.gaja.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GroupCreationServiceTest {
    GroupCreationService groupCreationService = new GroupCreationService();

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

        assertThatThrownBy(() -> groupCreationService.create("group", user)).isInstanceOf(GroupLimitExceededException.class);
    }
}