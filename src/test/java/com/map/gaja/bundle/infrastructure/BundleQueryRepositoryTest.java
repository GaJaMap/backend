package com.map.gaja.bundle.infrastructure;

import com.map.gaja.bundle.domain.model.Bundle;
import com.map.gaja.user.domain.model.Authority;
import com.map.gaja.user.domain.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class BundleQueryRepositoryTest {

    @Autowired
    BundleQueryRepository bundleQueryRepository;

    @Autowired
    EntityManager em;

    Long bundleId = null;
    String ownerEmail = "aaa@example.com";

    @BeforeEach
    void beforeEach() {
        User createdUser = User.builder()
                .email(ownerEmail)
                .authority(Authority.FREE)
                .bundleCount(0)
                .createdDate(LocalDateTime.now())
                .lastLoginDate(LocalDateTime.now())
                .build();
        em.persist(createdUser);

        Bundle createdBundle = Bundle.builder()
                .name("번들 1")
                .user(createdUser)
                .clientCount(0)
                .createdDate(LocalDateTime.now())
                .build();
        em.persist(createdBundle);

        bundleId = createdBundle.getId();
    }


    @Test
    @DisplayName("유저가 번들을 가지고 있는지 확인")
    void hasBundleByUserTrue() {
        boolean result = bundleQueryRepository.hasBundleByUser(bundleId, ownerEmail);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("유저가 번들을 가지고 없을 때 확인")
    void hasBundleByUserFalse() {
        String otherUserEmail = "bbb@example.com";
        boolean result = bundleQueryRepository.hasBundleByUser(bundleId, otherUserEmail);
        assertThat(result).isFalse();
    }
}