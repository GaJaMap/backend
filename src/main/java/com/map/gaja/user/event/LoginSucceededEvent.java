package com.map.gaja.user.event;

import lombok.Getter;

@Getter
public class LoginSucceededEvent {
    private final String email;
    private final String platformType;

    public LoginSucceededEvent(String email, String platformType) {
        this.email = email;
        this.platformType = platformType;
    }
}
