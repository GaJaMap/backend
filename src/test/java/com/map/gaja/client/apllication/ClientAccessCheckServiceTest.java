package com.map.gaja.client.apllication;

import com.map.gaja.bundle.domain.exception.BundleNotFoundException;
import com.map.gaja.bundle.domain.model.Bundle;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientAddress;
import com.map.gaja.client.domain.model.ClientLocation;
import com.map.gaja.client.presentation.dto.ClientAccessCheckDto;
import com.map.gaja.client.presentation.exception.ClientNotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ClientAccessCheckServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    ClientAccessVerifyService clientAccessVerifyService;

    User user;
    Bundle group;
    Client client;

    @BeforeEach
    void beforeEach() {
        user = createUser();
        em.persist(user);
        group = createGroup();
        em.persist(group);
        client = createClient();
        em.persist(client);
    }

    private User createUser() {
        return User.builder()
                .email("test@example.com")
                .authority(Authority.FREE)
                .bundleCount(0)
                .createdDate(LocalDateTime.now())
                .lastLoginDate(LocalDateTime.now())
                .build();
    }

    private Bundle createGroup() {
        return Bundle.builder()
                .name("번들 1")
                .user(user)
                .clientCount(0)
                .createdDate(LocalDateTime.now())
                .build();
    }

    Client createClient() {
        String sig = "1";
        String name = "사용자 " + sig;
        String phoneNumber = "010-1111-" + sig;
        ClientAddress address = new ClientAddress("aaa" + sig, "bbb" + sig, "ccc" + sig, "ddd" + sig);
        ClientLocation location = new ClientLocation(35d, 125.0d);
        return new Client(name, phoneNumber, address, location, group);
    }

    @Test
    @DisplayName("Client 접근 권한 검사 성공")
    void verifyClientAccess() {
        String email = user.getEmail();
        Long groupId = group.getId();
        Long clientId = client.getId();
        ClientAccessCheckDto accessRequest = new ClientAccessCheckDto(email, groupId, clientId);

        clientAccessVerifyService.verifyClientAccess(accessRequest);
    }

    @Test
    @DisplayName("Client 접근 권한 검사 - Group 오류")
    void verifyClientAccessGroupException() {
        String email = user.getEmail();
        Long wrongGroupId = -1L;
        Long clientId = client.getId();
        ClientAccessCheckDto accessRequest = new ClientAccessCheckDto(email, wrongGroupId, clientId);

        assertThrows(BundleNotFoundException.class ,
                () -> clientAccessVerifyService.verifyClientAccess(accessRequest));
    }

    @Test
    @DisplayName("Client 접근 권한 검사 - Client 오류")
    void verifyClientAccessClientException() {
        String email = user.getEmail();
        Long groupId = group.getId();
        Long wrongClientId = -1L;
        ClientAccessCheckDto accessRequest = new ClientAccessCheckDto(email, groupId, wrongClientId);

        assertThrows(ClientNotFoundException.class ,
                () -> clientAccessVerifyService.verifyClientAccess(accessRequest));
    }
}