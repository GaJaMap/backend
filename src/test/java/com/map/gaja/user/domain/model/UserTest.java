package com.map.gaja.user.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("날짜가 다르면 최근 접속일 update")
    void updateLastLoginDateSuccess() {
        LocalDateTime beforeDate = LocalDateTime.now().minus(2, ChronoUnit.DAYS);
        User user = User.builder()
                .lastLoginDate(beforeDate)
                .build();

        user.updateLastLoginDate();

        assertNotEquals(user.getLastLoginDate(), beforeDate);
    }

}