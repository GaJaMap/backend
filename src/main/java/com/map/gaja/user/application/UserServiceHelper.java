package com.map.gaja.user.application;

import com.map.gaja.user.domain.exception.UserNotFoundException;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;

public final class UserServiceHelper {
    public static User findExistingUser(UserRepository userRepository, String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    throw new UserNotFoundException();
                });
    }
}