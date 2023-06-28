package com.map.gaja.client.apllication;

import com.map.gaja.bundle.domain.exception.BundleNotFoundException;
import com.map.gaja.bundle.domain.model.Bundle;
import com.map.gaja.bundle.infrastructure.BundleQueryRepository;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientAddress;
import com.map.gaja.client.domain.model.ClientLocation;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
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
class ClientServiceHelperTest {

    @Autowired
    EntityManager em;

    @Autowired
    BundleQueryRepository bundleQueryRepository;

    @Autowired
    ClientQueryRepository clientQueryRepository;

    User user;
    Bundle bundle;
    Client client;

    @BeforeEach
    void beforeEach() {
        user = createUser();
        em.persist(user);
        bundle = createBundle();
        em.persist(bundle);
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

    private Bundle createBundle() {
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
        return new Client(name, phoneNumber, address, location, bundle);
    }

    @Test
    @DisplayName("Client 접근 권한 검사 성공")
    void verifyClientAccess() {
        String email = user.getEmail();
        Long bundleId = bundle.getId();
        Long clientId = client.getId();
        ClientAccessCheckDto accessRequest = new ClientAccessCheckDto(email, bundleId, clientId);

        ClientServiceHelper.verifyClientAccess(bundleQueryRepository, clientQueryRepository, accessRequest);
    }

    @Test
    @DisplayName("Client 접근 권한 검사 - Bundle 오류")
    void verifyClientAccessBundleException() {
        String email = user.getEmail();
        Long wrongBundleId = -1L;
        Long clientId = client.getId();
        ClientAccessCheckDto accessRequest = new ClientAccessCheckDto(email, wrongBundleId, clientId);

        assertThrows(BundleNotFoundException.class ,
                () -> ClientServiceHelper.verifyClientAccess(bundleQueryRepository, clientQueryRepository, accessRequest));
    }

    @Test
    @DisplayName("Client 접근 권한 검사 - Client 오류")
    void verifyClientAccessClientException() {
        String email = user.getEmail();
        Long bundleId = bundle.getId();
        Long wrongClientId = -1L;
        ClientAccessCheckDto accessRequest = new ClientAccessCheckDto(email, bundleId, wrongClientId);

        assertThrows(ClientNotFoundException.class ,
                () -> ClientServiceHelper.verifyClientAccess(bundleQueryRepository, clientQueryRepository, accessRequest));
    }
}