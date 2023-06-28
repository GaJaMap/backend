package com.map.gaja.client.infrastructure.repository;

import com.map.gaja.bundle.domain.model.Bundle;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientAddress;
import com.map.gaja.client.domain.model.ClientLocation;
import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import com.map.gaja.user.domain.model.Authority;
import com.map.gaja.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class ClientQueryRepositoryTest {

    @Autowired
    ClientQueryRepository clientQueryRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    EntityManager em;

    User user;
    Bundle bundle1, bundle2;
    List<Client> bundle1ClientList, bundle2ClientList;

    @BeforeEach
    void before() {
        user = createUser();
        em.persist(user);

        bundle1 = createBundle("bundle1");
        bundle2 = createBundle("bundle2");
        em.persist(bundle1);
        em.persist(bundle2);

        bundle1ClientList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String sig = i+""+i;
            Client client = createClient(i, 0.003, bundle1);
            bundle1ClientList.add(client);
        }
        clientRepository.saveAll(bundle1ClientList);

        bundle2ClientList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Client client = createClient(i, 0.005, bundle2);
            bundle2ClientList.add(client);
        }
        clientRepository.saveAll(bundle2ClientList);
    }

    private Client createClient(int sigIdx, double pointSig, Bundle bundle1) {
        String sig = sigIdx+""+sigIdx;
        String name = "사용자 " + sig;
        String phoneNumber = "010-1111-" + sig;
        ClientAddress address = new ClientAddress("aaa" + sig, "bbb" + sig, "ccc" + sig, "ddd" + sig);
        ClientLocation location = new ClientLocation(35d + pointSig * sigIdx, 125.0d + pointSig * sigIdx);
        Client client = new Client(name, phoneNumber, address, location, bundle1);
        return client;
    }

    private Bundle createBundle(String bundleName) {
        Bundle createdBundle = Bundle.builder()
                .name(bundleName)
                .user(user)
                .clientCount(0)
                .createdDate(LocalDateTime.now())
                .build();
        return createdBundle;
    }

    private User createUser() {
        User createdUser = User.builder()
                .email("test@example.com")
                .authority(Authority.FREE)
                .bundleCount(0)
                .createdDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .lastLoginDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();

        return createdUser;
    }


    @Test
    @DisplayName("위치정보 없이 검색")
    void findClientWithoutGPSTest() {
        Pageable pageable = PageRequest.of(0, 10);
        String nameKeyword = "사용자";
        Long bundleId = bundle2.getId();
        Slice<ClientResponse> result = clientQueryRepository.findClientByConditions(bundleId, null,nameKeyword,pageable);
        List<ClientResponse> content = result.getContent();

        assertThat(result.getSize()).isEqualTo(10);

        for (ClientResponse client : content) {
//            System.out.println("client = " + client);
            assertThat(client.getClientName()).contains(nameKeyword);
            assertThat(client.getDistance()).isEqualTo(-1);
            assertThat(client.getBundleId()).isEqualTo(bundleId);
        }
    }

    @Test
    @DisplayName("위치정보 포함 검색")
    void findClientWithGPSTest() {
        String nameKeyword = "사용자";
        Long bundleId = bundle2.getId();
        double radius = 3000;
        NearbyClientSearchRequest request = new NearbyClientSearchRequest(new LocationDto(35.006, 125.006), radius);
        Pageable pageable = PageRequest.of(0, 10);

        Slice<ClientResponse> result = clientQueryRepository.findClientByConditions(bundleId, request,nameKeyword,pageable);
        List<ClientResponse> content = result.getContent();

        assertThat(result.getSize()).isEqualTo(10);

        for (ClientResponse client : content) {
//            System.out.println("client = " + client);
            assertThat(client.getClientName()).contains(nameKeyword);
            assertThat(client.getDistance()).isLessThan(radius);
            assertThat(client.getBundleId()).isEqualTo(bundleId);
        }
    }

    @Test
    @DisplayName("User, Bundle을 이용해서 Client 조회")
    void findClientWithBundleAndUserTest() {
        String loginEmail = user.getEmail();
        Long clientId = bundle1ClientList.get(0).getId();
        Long bundleId = bundle1.getId();

        Client result = clientQueryRepository.findClientByUserAndBundle(loginEmail, bundleId, clientId)
                .orElseThrow(() -> new IllegalArgumentException());

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("User, Bundle를 이용해서 Client 조회 실패")
    void findClientWithBundleAndUserFailTest() {
        String loginEmail = user.getEmail();
        Long clientId = bundle2ClientList.get(0).getId();
        Long bundleId = bundle1.getId();

        Optional<Client> result = clientQueryRepository.findClientByUserAndBundle(loginEmail, bundleId, clientId);

        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Client가 번들에 속해있을때")
    void hasClientByBundleTrue() {
        boolean result = clientQueryRepository.hasClientByBundle(bundle1.getId(), bundle1ClientList.get(0).getId());
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Client가 번들에 속해있지 않을때")
    void hasClientByBundleFalse() {
        boolean result = clientQueryRepository.hasClientByBundle(bundle1.getId(), bundle2ClientList.get(0).getId());
        assertThat(result).isFalse();
    }

}