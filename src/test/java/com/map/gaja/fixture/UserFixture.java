package com.map.gaja.fixture;

import com.map.gaja.user.domain.model.Authority;
import com.map.gaja.user.domain.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class UserFixture {

    /**
     * Free등급 사용자 생성
     */
    public static User createFreeUser() {
        return User.builder()
                .id(1L)
                .email("test@gmail.com")
                .active(true)
                .authority(Authority.FREE)
                .groupCount(0)
                .lastLoginDate(LocalDateTime.now())
                .build();
    }

    /**
     * 회원 탈퇴한 사용자 생성
     */
    public static User createWithdrawnUser() {
        return User.builder()
                .email("test@gmail.com")
                .active(false)
                .build();
    }

    /**
     * 최근 접속 날짜를 변경할 수 있는 사용자 생성
     */
    public static User createUserWithCustomLastLogin(LocalDateTime localDateTime) {
        return User.builder()
                .email("test@gmail.com")
                .active(true)
                .authority(Authority.FREE)
                .groupCount(0)
                .lastLoginDate(localDateTime)
                .build();
    }
}
