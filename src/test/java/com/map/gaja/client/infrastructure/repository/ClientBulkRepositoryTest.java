package com.map.gaja.client.infrastructure.repository;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientAddress;
import com.map.gaja.client.domain.model.ClientLocation;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.user.domain.model.Authority;
import com.map.gaja.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
class ClientBulkRepositoryTest {

    @Autowired
    ClientBulkRepository repository;

    @Autowired
    EntityManager em;

    User user;
    Group group;
    List<Client> clientList;

    @BeforeEach
    void beforeEach() {
        user = createUser();
        group = createGroup("test", user);
        em.persist(user);
        em.persist(group);

        clientList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Client client = createClient(i, 0.003, group);
            clientList.add(client);
        }
    }

//    @Test
    void batchInsert() {
        repository.insertClientWithGroup(group, clientList);
    }

    private Group createGroup(String groupName, User user) {
        Group createdGroup = Group.builder()
                .name(groupName)
                .user(user)
                .clientCount(0)
                .isDeleted(false)
                .build();
        return createdGroup;
    }

    private Client createClient(int sigIdx, double pointSig, Group group) {
        String sig = sigIdx+""+sigIdx;
        String name = "사용자 " + sig;
        String phoneNumber = "010-1111-" + sig;
        ClientAddress address = new ClientAddress("address " + sig, "detail " + sig);
        ClientLocation location = new ClientLocation(35d + pointSig * sigIdx, 125.0d + pointSig * sigIdx);
        Client client = new Client(name, phoneNumber, address, location, group);
        return client;
    }

    private User createUser() {
        User createdUser = User.builder()
                .email("test@example.com")
                .authority(Authority.FREE)
                .groupCount(0)
                .lastLoginDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();

        return createdUser;
    }
}