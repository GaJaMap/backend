package com.map.gaja.client.infrastructure.repository;

import com.map.gaja.TestEntityCreator;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.user.domain.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
class ClientBulkNativeRepositoryTest {

    @Autowired
    ClientBulkRepository repository;

    @Autowired
    EntityManager em;

    User user;
    Group group;
    List<Client> clientList;

    @BeforeEach
    void beforeEach() {
        user = TestEntityCreator.createUser("test@example.com");
        group = TestEntityCreator.createGroup(user, "test");
        em.persist(user);
        em.persist(group);
        em.flush();
        em.clear();

        clientList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Client client = TestEntityCreator.createClient(i, group);
            clientList.add(client);
        }
    }

    @Test
//    @Rollback(value = false)
    void saveClientWithGroup() {
        repository.saveClientWithGroup(group, clientList);
        Long resultSize = em.createQuery("Select Count(c) From Client c Where c.group.id = :groupId", Long.class)
                .setParameter("groupId", group.getId())
                .getSingleResult();

        Assertions.assertThat(resultSize).isEqualTo(clientList.size());
    }
}