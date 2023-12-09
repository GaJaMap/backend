package com.map.gaja.user.application;

import com.map.gaja.user.domain.exception.UserNotFoundException;
import com.map.gaja.user.domain.exception.WithdrawalUserException;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserServiceHelper {
    /**
     * 회은 탈퇴 안 한 유저 조회
     */
    public static User findByEmailAndActive(UserRepository userRepository, String email) {
        return userRepository.findByEmailAndActive(email)
                .orElseThrow(() -> {
                    throw new UserNotFoundException();
                });
    }

    /**
     * 소셜 로그인 시 신규유저인지 회원 탈퇴 유저인지 판별
     */
    public static User findByEmail(UserRepository userRepository, String email) {
        User user = userRepository.findByEmail(email);
        if (isWithdrawalUser(user)) {
            throw new WithdrawalUserException();
        }

        if (isNewUser(user)) {
            user = new User(email);
            userRepository.save(user);
        }

        return user;
    }

    /**
     * Lock과 함께 유저 조회
     */
    public static User findByEmailAndActiveWithLock(UserRepository userRepository, Long userId) {
        return userRepository.findByEmailAndActiveForUpdate(userId)
                .orElseThrow(() -> {
                    throw new UserNotFoundException();
                });
    }

    /**
     * userId로 유저 조회
     */
    public static User findById(UserRepository userRepository, Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    throw new UserNotFoundException();
                });
    }

    private static boolean isNewUser(User user) {
        if (user == null) {
            return true;
        }
        return false;
    }

    private static boolean isWithdrawalUser(User user) {
        if (user != null && !user.getActive()) {
            return true;
        }
        return false;
    }
}