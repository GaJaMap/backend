package com.map.gaja.group.infrastructure;

import com.map.gaja.common.RepositoryTest;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.user.domain.model.Authority;
import com.map.gaja.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;


class GroupQueryRepositoryTest extends RepositoryTest{
    @Autowired
    GroupQueryRepository groupQueryRepository;

    @Autowired
    EntityManager em;

    Long groupId = null;
    String ownerEmail = "aaa@example.com";

    @BeforeEach
    void beforeEach() {
        User createdUser = User.builder()
                .email(ownerEmail)
                .authority(Authority.FREE)
                .groupCount(0)
                .lastLoginDate(LocalDateTime.now())
                .build();
        em.persist(createdUser);

        Group createdGroup = Group.builder()
                .name("그룹 1")
                .user(createdUser)
                .clientCount(0)
                .isDeleted(false)
                .build();
        em.persist(createdGroup);

        groupId = createdGroup.getId();
    }


    @Test
    @DisplayName("유저가 그룹을 가지고 있는지 확인")
    void hasGroupByUserTrue() {
        boolean result = groupQueryRepository.hasGroupByUser(groupId, ownerEmail);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("유저가 그룹을 가지고 없을 때 확인")
    void hasGroupByUserFalse() {
        String otherUserEmail = "bbb@example.com";
        boolean result = groupQueryRepository.hasGroupByUser(groupId, otherUserEmail);
        assertThat(result).isFalse();
    }
}