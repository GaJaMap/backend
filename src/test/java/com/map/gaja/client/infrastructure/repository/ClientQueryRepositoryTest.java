package com.map.gaja.client.infrastructure.repository;

import com.map.gaja.group.domain.model.Group;
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
    Group group1, group2;
    List<Client> group1ClientList, group2ClientList;
    double radius = 1000000;

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
        em.flush();
        em.clear();
    }

    private Client createClient(int sigIdx, double pointSig, Group group) {
        String sig = sigIdx+""+sigIdx;
        String name = "사용자 " + sig;
        String phoneNumber = "010-1111-" + sig;
        ClientAddress address = new ClientAddress("aaa" + sig, "bbb" + sig, "ccc" + sig, "ddd" + sig);
        ClientLocation location = new ClientLocation(35d + pointSig * sigIdx, 125.0d + pointSig * sigIdx);
        Client client = new Client(name, phoneNumber, address, location, group);
        return client;
    }

    private Group createGroup(String groupName) {
        Group createdGroup = Group.builder()
                .name(groupName)
                .user(user)
                .clientCount(0)
                .build();
        return createdGroup;
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

    @Test
    @DisplayName("위치 정보 포함 반경 검색")
    void findClientWithGPSTest() {
        String nameKeyword = "사용자";
        Long groupId = group2.getId();
        String groupName = group2.getName();
        NearbyClientSearchRequest request = new NearbyClientSearchRequest(new LocationDto(35.006, 125.006), radius);
        List<Long> groupIdList = new ArrayList<>();
        groupIdList.add(groupId);

        List<ClientResponse> result = clientQueryRepository.findClientByConditions(groupIdList, request,nameKeyword);

        assertThat(result.size()).isEqualTo(group2ClientList.size());
        double beforeDistance = -1;
        result.forEach((client) -> {
            assertThat(client.getDistance()).isGreaterThan(beforeDistance);

            assertThat(client.getClientName()).contains(nameKeyword);
            assertThat(client.getDistance()).isLessThan(radius);
            assertThat(client.getGroupInfo().getGroupId()).isEqualTo(groupId);
            assertThat(client.getGroupInfo().getGroupName()).isEqualTo(groupName);
        });
    }

    @Test
    @DisplayName("다중 번들 반경 검색")
    void findClientWithoutGroupIdTest() {
        String nameKeyword = "사용자";
        Long groupId1 = group1.getId();
        Long groupId2 = group2.getId();
        System.out.println("groupId1 = " + groupId1);
        System.out.println("groupId2 = " + groupId2);
        String groupName1 = group1.getName();
        String groupName2 = group2.getName();
        NearbyClientSearchRequest request = new NearbyClientSearchRequest(new LocationDto(35.006, 125.006), radius);
        List<Long> groupIdList = new ArrayList<>();
        groupIdList.add(groupId1);
        groupIdList.add(groupId2);

        List<ClientResponse> result = clientQueryRepository.findClientByConditions(groupIdList, request,nameKeyword);

        assertThat(result.size()).isEqualTo(group1ClientList.size() + group2ClientList.size());
        result.forEach((client) -> {
            System.out.println("client = " + client.getGroupInfo().getGroupId());
            assertThat(client.getClientName()).contains(nameKeyword);
            assertThat(client.getDistance()).isLessThan(radius);
            assertThat(client.getGroupInfo().getGroupId()).isIn(groupId1, groupId2);
            assertThat(client.getGroupInfo().getGroupName()).isIn(groupName1, groupName2);
        });
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

    @Test
    @DisplayName("그룹 ID로 Client 조회 성공")
    void findByGroup_IdTest() {
        List<Client> clientInGroup1 = clientQueryRepository.findByGroup_Id(group1.getId());

        clientInGroup1.forEach((client -> {
            assertThat(client.getGroup().getName()).isEqualTo(group1.getName());
        }));
    }

    @Test
    @DisplayName("로그인 한 유저가 가지고 있는 Client 검색")
    void ss() {
        String nameCond = "사용자";

        List<ClientResponse> result = clientQueryRepository.findAllClientByEmail(user.getEmail(), nameCond);

        assertThat(result.size()).isEqualTo(group1ClientList.size() + group2ClientList.size());
        result.forEach((client) -> {
            assertThat(client.getClientName()).contains(nameCond);
        });
    }
}