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
    Bundle group1, group2;
    List<Client> group1ClientList, group2ClientList;

    @BeforeEach
    void before() {
        user = createUser();
        em.persist(user);

        group1 = createGroup("group1");
        group2 = createGroup("group2");
        em.persist(group1);
        em.persist(group2);

        group1ClientList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String sig = i+""+i;
            Client client = createClient(i, 0.003, group1);
            group1ClientList.add(client);
        }
        clientRepository.saveAll(group1ClientList);

        group2ClientList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Client client = createClient(i, 0.005, group2);
            group2ClientList.add(client);
        }
        clientRepository.saveAll(group2ClientList);
    }

    private Client createClient(int sigIdx, double pointSig, Bundle group) {
        String sig = sigIdx+""+sigIdx;
        String name = "사용자 " + sig;
        String phoneNumber = "010-1111-" + sig;
        ClientAddress address = new ClientAddress("aaa" + sig, "bbb" + sig, "ccc" + sig, "ddd" + sig);
        ClientLocation location = new ClientLocation(35d + pointSig * sigIdx, 125.0d + pointSig * sigIdx);
        Client client = new Client(name, phoneNumber, address, location, group);
        return client;
    }

    private Bundle createGroup(String groupName) {
        Bundle createdGroup = Bundle.builder()
                .name(groupName)
                .user(user)
                .clientCount(0)
                .createdDate(LocalDateTime.now())
                .build();
        return createdGroup;
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
    @DisplayName("위치 정보 없이 반경 검색")
    void findClientWithoutGPSTest() {
        String nameKeyword = "사용자";
        Long groupId = group2.getId();
        List<Long> groupIdList = new ArrayList<>();
        groupIdList.add(groupId);
        List<ClientResponse> result = clientQueryRepository.findClientByConditions(groupIdList, null,nameKeyword);

        for (ClientResponse client : result) {
//            System.out.println("client = " + client);
            assertThat(client.getClientName()).contains(nameKeyword);
            assertThat(client.getDistance()).isEqualTo(-1);
            assertThat(client.getGroupId()).isEqualTo(groupId);
        }
    }

    @Test
    @DisplayName("위치 정보 포함 반경 검색")
    void findClientWithGPSTest() {
        String nameKeyword = "사용자";
        Long groupId = group2.getId();
        double radius = 3000;
        NearbyClientSearchRequest request = new NearbyClientSearchRequest(new LocationDto(35.006, 125.006), radius);
        List<Long> groupIdList = new ArrayList<>();
        groupIdList.add(groupId);

        List<ClientResponse> result = clientQueryRepository.findClientByConditions(groupIdList, request,nameKeyword);

        for (ClientResponse client : result) {
//            System.out.println("client = " + client);
            assertThat(client.getClientName()).contains(nameKeyword);
            assertThat(client.getDistance()).isLessThan(radius);
            assertThat(client.getGroupId()).isEqualTo(groupId);
        }
    }

    @Test
    @DisplayName("다중 번들 반경 검색")
    void findClientWithoutgroupIdTest() {
        String nameKeyword = "사용자";
        Long groupId1 = group1.getId();
        Long groupId2 = group2.getId();
        double radius = 3000;
        NearbyClientSearchRequest request = new NearbyClientSearchRequest(new LocationDto(35.006, 125.006), radius);
        List<Long> groupIdList = new ArrayList<>();
        groupIdList.add(groupId1);
        groupIdList.add(groupId2);

        List<ClientResponse> result = clientQueryRepository.findClientByConditions(groupIdList, request,nameKeyword);

        for (ClientResponse client : result) {
//            System.out.println("client = " + client);
            assertThat(client.getClientName()).contains(nameKeyword);
            assertThat(client.getDistance()).isLessThan(radius);
            assertThat(client.getGroupId()).isIn(groupId1, groupId2);
        }
    }

    @Test
    @DisplayName("User, Group을 이용해서 Client 조회")
    void findClientWithGroupAndUserTest() {
        String loginEmail = user.getEmail();
        Long clientId = group1ClientList.get(0).getId();
        Long groupId = group1.getId();

        Client result = clientQueryRepository.findClientByUserAndGroup(loginEmail, groupId, clientId)
                .orElseThrow(() -> new IllegalArgumentException());

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("User, Group를 이용해서 Client 조회 실패")
    void findClientWithGroupAndUserFailTest() {
        String loginEmail = user.getEmail();
        Long clientId = group2ClientList.get(0).getId();
        Long groupId = group1.getId();

        Optional<Client> result = clientQueryRepository.findClientByUserAndGroup(loginEmail, groupId, clientId);

        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Client가 그룹에 속해있을때")
    void hasClientByGroupTrue() {
        boolean result = clientQueryRepository.hasClientByGroup(group1.getId(), group1ClientList.get(0).getId());
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Client가 번들에 속해있지 않을때")
    void hasClientByGroupFalse() {
        boolean result = clientQueryRepository.hasClientByGroup(group1.getId(), group2ClientList.get(0).getId());
        assertThat(result).isFalse();
    }

}