package com.map.gaja.user.infrastructure;

import com.map.gaja.common.RepositoryTest;
import com.map.gaja.fixture.UserFixture;
import com.map.gaja.user.domain.model.User;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest extends RepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("최근 접속 일을 변경한다.")
    void updateLastLoginDate() {
        // given
        LocalDateTime lastLoginTime = LocalDateTime.now().minus(2, ChronoUnit.DAYS);
        User user = UserFixture.createUserWithCustomLastLogin(lastLoginTime);
        userRepository.save(user);
        em.clear();

        Long userId = user.getId();
        LocalDateTime now = LocalDateTime.now();

        // when
        userRepository.updateLastLoginDate(userId, now);
        User targetUser = userRepository.findById(userId).get();

        // then
        assertThat(targetUser.getLastLoginDate().truncatedTo(ChronoUnit.SECONDS))
                .isEqualTo(now.truncatedTo(ChronoUnit.SECONDS));
    }
}