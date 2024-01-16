package com.map.gaja.user.infrastructure;

import com.map.gaja.user.domain.model.Authority;
import com.map.gaja.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("최근 접속 일을 변경한다.")
    void updateLastLoginDate() {
        // given
        User user = User.builder()
                .email("test")
                .active(true)
                .authority(Authority.FREE)
                .groupCount(0)
                .lastLoginDate(LocalDateTime.now().minus(2, ChronoUnit.DAYS))
                .build();
        userRepository.save(user);
        em.flush();
        em.clear();

        LocalDateTime now = LocalDateTime.now();

        // when
        userRepository.updateLastLoginDate(user.getId(), now);
        user = userRepository.findByEmailAndActive("test").get();

        // then
        assertThat(user.getLastLoginDate().truncatedTo(ChronoUnit.SECONDS)).isEqualTo(now.truncatedTo(ChronoUnit.SECONDS));
    }
}