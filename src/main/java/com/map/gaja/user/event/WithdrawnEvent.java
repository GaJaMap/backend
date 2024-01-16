package com.map.gaja.user.event;

import lombok.Getter;

@Getter
public class WithdrawnEvent {
    private final String email;

    public WithdrawnEvent(String email) {
        this.email = email;
    }
}
