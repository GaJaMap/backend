package com.map.gaja.client.apllication;

import com.map.gaja.client.domain.exception.ClientNotFoundException;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.presentation.dto.access.ClientAccessCheckDto;
import com.map.gaja.client.presentation.dto.access.ClientListAccessCheckDto;
import com.map.gaja.group.domain.exception.GroupNotFoundException;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.user.domain.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@Transactional
class ClientAccessVerifyServiceTest {
    @Autowired
    EntityManager em;

    @Autowired
    ClientAccessVerifyService clientAccessVerifyService;

    String email = "test@example.com";
    User user;
    Group group;
    Client client;

    @BeforeEach
    void beforeEach() {
        user = createUser();
        em.persist(user);

        group = createGroup(user);
        em.persist(group);

        client = createClient(group, 1);
        em.persist(client);
    }

    @Test
    @DisplayName("단일 Client 접근 검증 성공")
    void verifyClientAccessTest() {
        ClientAccessCheckDto accessRequest = new ClientAccessCheckDto(email, group.getId(), client.getId());
        clientAccessVerifyService.verifyClientAccess(accessRequest);
    }

    @Test
    @DisplayName("단일 잘못된 ClientId 검증")
    void verifyFailClientIdTest() {
        long failId = -1;
        ClientAccessCheckDto accessRequest = new ClientAccessCheckDto(email, group.getId(), failId);
        Assertions.assertThrows(ClientNotFoundException.class,
                () -> clientAccessVerifyService.verifyClientAccess(accessRequest));
    }

    @Test
    @DisplayName("단일 잘못된 GroupId 찾기 검증")
    void verifyFailGroupTest() {
        long failId = -1;
        ClientAccessCheckDto accessRequest = new ClientAccessCheckDto(email, failId, client.getId());
        Assertions.assertThrows(GroupNotFoundException.class,
                () -> clientAccessVerifyService.verifyClientAccess(accessRequest));
    }

    @Test
    @DisplayName("다중 Client 접근 검증")
    void verifyClientListAccessTest() {
        List<Client> clientList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Client clientOther = createClient(group, i);
            clientList.add(clientOther);
            em.persist(clientOther);
        }

        List<Long> savedClientIds = clientList.stream().map(Client::getId).collect(Collectors.toList());

        ClientListAccessCheckDto accessRequest = new ClientListAccessCheckDto(email, group.getId(), savedClientIds);
        clientAccessVerifyService.verifyClientListAccess(accessRequest);
    }

    @Test
    @DisplayName("다중 잘못된 ClientId 검증")
    void verifyListFailClientIdTest() {
        List<Client> clientList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Client clientOther = createClient(group, i);
            clientList.add(clientOther);
            em.persist(clientOther);
        }

        List<Long> savedClientIds = clientList.stream().map(Client::getId).collect(Collectors.toList());
        long failIdx = -1;
        savedClientIds.add(failIdx);

        ClientListAccessCheckDto accessRequest = new ClientListAccessCheckDto(email, group.getId(), savedClientIds);
        Assertions.assertThrows(ClientNotFoundException.class,
                () -> clientAccessVerifyService.verifyClientListAccess(accessRequest));
    }

    @Test
    @DisplayName("다중 잘못된 GroupId 검증")
    void verifyListFailGroupIdTest() {
        List<Client> clientList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Client clientOther = createClient(group, i);
            clientList.add(clientOther);
            em.persist(clientOther);
        }

        List<Long> savedClientIds = clientList.stream().map(Client::getId).collect(Collectors.toList());
        long failIdx = -1;

        ClientListAccessCheckDto accessRequest = new ClientListAccessCheckDto(email, failIdx, savedClientIds);
        Assertions.assertThrows(GroupNotFoundException.class,
                () -> clientAccessVerifyService.verifyClientListAccess(accessRequest));
    }

    private User createUser() {
        return new User(email);
    }

    private static Client createClient(Group g1, int idx) {
        return new Client("사용자 " + idx, "010-1111-2222", g1);
    }

    private static Group createGroup(User user) {
        return Group.builder().user(user).name("그룹").clientCount(0).isDeleted(false).build();
    }
}