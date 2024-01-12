package com.map.gaja.user.event;

import com.map.gaja.global.authentication.SessionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEventListener {
    private final SessionHandler sessionHandler;

    @EventListener(LoginSucceededEvent.class)
    public void login(LoginSucceededEvent event) {
        sessionHandler.deduplicate(event.getEmail(), event.getPlatformType());
    }

    @EventListener(WithdrawnEvent.class)
    public void withdrawal(WithdrawnEvent event) {
        sessionHandler.deleteAllByEmail(event.getEmail());
    }
}
