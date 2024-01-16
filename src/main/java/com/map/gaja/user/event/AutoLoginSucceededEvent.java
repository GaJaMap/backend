package com.map.gaja.user.event;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AutoLoginSucceededEvent {
    private final Long userId;
    private final LocalDateTime lastLoginDate;

    public AutoLoginSucceededEvent(Long userId, LocalDateTime lastLoginDate) {
        this.userId = userId;
        this.lastLoginDate = lastLoginDate;
    }
}
