package com.map.gaja.client.infrastructure.repository;

import com.map.gaja.TestEntityCreator;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ClientRepositoryTest {
    @Autowired
    ClientRepository clientRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("User가 가진 Client를 조회한다.")
    void findByIdAndUserSuccess() {
        // given
        User user = TestEntityCreator.createUser("test@gmail.com");
        Group group = TestEntityCreator.createGroup(user, "group");
        em.persist(user);
        em.persist(group);
        Client client = clientRepository.save(TestEntityCreator.createClient(0, group, user));

        // when, then
        assertThat(clientRepository.findByIdAndUser(client.getId(), user.getId()).get())
                .isNotNull();
    }

    @Test
    @DisplayName("User가 가진 Client의 조회 실패한다.")
    void findByIdAndUserFail() {
        // given
        User user = TestEntityCreator.createUser("test@gmail.com");
        Group group = TestEntityCreator.createGroup(user, "group");
        em.persist(user);
        em.persist(group);
        Client client = clientRepository.save(TestEntityCreator.createClient(0, group, user));

        // when, then
        assertThat(clientRepository.findByIdAndUser(client.getId() + 1, user.getId()).isEmpty())
                .isTrue();
    }
}