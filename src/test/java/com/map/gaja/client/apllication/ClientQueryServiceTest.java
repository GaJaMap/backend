package com.map.gaja.client.apllication;

import com.map.gaja.TestEntityCreator;
import com.map.gaja.client.domain.model.ClientImage;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.infrastructure.s3.S3UrlGenerator;
import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import com.map.gaja.client.presentation.dto.response.ClientDetailResponse;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientAddress;
import com.map.gaja.client.domain.model.ClientLocation;
import com.map.gaja.client.presentation.dto.response.ClientOverviewResponse;
import com.map.gaja.client.domain.exception.ClientNotFoundException;
import com.map.gaja.group.infrastructure.GroupQueryRepository;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientQueryServiceTest {

    @Mock
    private ClientQueryRepository repository;

    @Mock
    private S3UrlGenerator s3UrlGenerator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupQueryRepository groupQueryRepository;

    @InjectMocks
    private ClientQueryService clientQueryService;

    @Test
    public void testFindUser() throws Exception {
        //given
        Long searchId = 1L;
        Long groupId = 1L;
        String searchName = "test";
        Client findClient = mock(Client.class);
        when(repository.findClientWithGroup(searchId)).thenReturn(Optional.ofNullable(findClient));
        when(findClient.getId()).thenReturn(searchId);
        when(findClient.getName()).thenReturn(searchName);
        when(findClient.getAddress()).thenReturn(new ClientAddress());
        when(findClient.getLocation()).thenReturn(new ClientLocation());
        when(findClient.getGroup()).thenReturn(Group.builder().id(groupId).build());

        String urlPrefix = "s3.aaa.bbb/";
        String savedPath = "testImage";
        when(findClient.getClientImage()).thenReturn(new ClientImage("testImage", savedPath));
        when(s3UrlGenerator.getS3Url()).thenReturn(urlPrefix);

        //when
        ClientDetailResponse response = clientQueryService.findClient(searchId);

        //then
        assertThat(response.getClientId()).isEqualTo(searchId);
        assertThat(response.getClientName()).isEqualTo(searchName);
        assertThat(response.getImage().getFilePath()).isEqualTo(urlPrefix + savedPath);
    }

    @Test
    public void testFindUserNotFound() throws Exception {
        //given
        Long searchId = 1L;
        String searchName = "test";
        Client findClient = mock(Client.class);
        when(repository.findClientWithGroup(searchId)).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> clientQueryService.findClient(searchId))
                .isInstanceOf(ClientNotFoundException.class);
    }

    @Test
    @DisplayName("전체 고객 조회")
    void findAllClientTest() {
        String testEmail = "test@example.com";
        Long groupId1 = 1L;
        Long groupId2 = 2L;
        User testUser = TestEntityCreator.createUser(testEmail);
        testUser.accessGroup(groupId1);
        Group testGroup1 = TestEntityCreator.createGroup(testUser, groupId1, "Test Group", 2);
        Group testGroup2 = TestEntityCreator.createGroup(testUser, groupId2, "Test Group", 2);

        List<Client> clientList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            clientList.add(TestEntityCreator.createClient(i, testGroup1));
            clientList.add(TestEntityCreator.createClient(i, testGroup2));
        }
        List<ClientOverviewResponse> testList = clientList.stream().map(ClientConvertor::entityToOverviewDto)
                .collect(Collectors.toList());

        when(userRepository.findByEmail(testEmail)).thenReturn(testUser);
        when(repository.findActiveClientByEmail(testEmail, null)).thenReturn(testList);

        ClientListResponse result = clientQueryService.findAllClient(testEmail, null);


        assertThat(testUser.getReferenceGroupId()).isNull();
        result.getClients().forEach(response -> {
            assertThat(response.getGroupInfo().getGroupId()).isIn(groupId1, groupId2);
        });
    }

    @Test
    @DisplayName("전체 고객 반경 조회")
    void findClientByConditionsTest() {
        String testEmail = "test@example.com";
        Long groupId1 = 1L;
        Long groupId2 = 2L;
        User testUser = TestEntityCreator.createUser(testEmail);
        testUser.accessGroup(groupId1);
        Group testGroup1 = TestEntityCreator.createGroup(testUser, groupId1, "Test Group", 2);
        Group testGroup2 = TestEntityCreator.createGroup(testUser, groupId2, "Test Group", 2);
        NearbyClientSearchRequest locationSearchCond = new NearbyClientSearchRequest(new LocationDto(35d, 127d), 3000);

        List<Long> groupIdList = List.of(groupId1, groupId2);
        List<Client> clientList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            clientList.add(TestEntityCreator.createClient(i, testGroup1));
            clientList.add(TestEntityCreator.createClient(i, testGroup2));
        }
        List<ClientOverviewResponse> testList = clientList.stream().map(ClientConvertor::entityToOverviewDto)
                .collect(Collectors.toList());

        when(userRepository.findByEmail(testEmail)).thenReturn(testUser);
        when(groupQueryRepository.findActiveGroupId(testEmail)).thenReturn(groupIdList);
        when(repository.findClientByConditions(groupIdList, locationSearchCond, null)).thenReturn(testList);


        ClientListResponse result = clientQueryService.findClientByConditions(testEmail, locationSearchCond,null);

        assertThat(testUser.getReferenceGroupId()).isNull();
        result.getClients().forEach(response -> {
            assertThat(response.getGroupInfo().getGroupId()).isIn(groupId1, groupId2);
        });
    }

}