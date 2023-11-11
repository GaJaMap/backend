package com.map.gaja.client.apllication;

import com.map.gaja.TestEntityCreator;
import com.map.gaja.client.domain.model.ClientImage;
import com.map.gaja.client.infrastructure.file.parser.dto.ParsedClientDto;
import com.map.gaja.client.infrastructure.repository.ClientBulkRepository;
import com.map.gaja.client.presentation.dto.request.simple.SimpleClientBulkRequest;
import com.map.gaja.client.presentation.dto.request.simple.SimpleNewClientRequest;
import com.map.gaja.client.presentation.dto.response.ClientOverviewResponse;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import com.map.gaja.global.authentication.CurrentSecurityUserGetter;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.domain.service.IncreasingClientService;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.user.domain.model.Authority;
import com.map.gaja.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class ClientServiceTest {
    ClientService clientService;

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ClientBulkRepository clientBulkRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private ClientQueryRepository clientQueryRepository;
    IncreasingClientService increasingClientService = new IncreasingClientService();
    @Mock
    CurrentSecurityUserGetter securityUserGetter;

    Long clientId = 1L;
    Long groupId = 1L;
    Long changedGroupId = 2L;
    Long existingClientId = 1L;
    String existingName = "test";
    String changedName = "update Test";
    String email = "testEmail";

    User user;
    Group existingGroup;
    Group changedGroup;
    Client existingClient;
    ClientImage clientImage;

    @BeforeEach
    void beforeEach() {
        clientService = new ClientService(
                clientRepository,
                clientBulkRepository,
                groupRepository,
                clientQueryRepository,
                increasingClientService,
                securityUserGetter
        );

        user = TestEntityCreator.createUser(email);
        existingGroup = TestEntityCreator.createGroup(user, groupId, "Test Group1", 1);
        changedGroup = TestEntityCreator.createGroup(user, groupId, "Test Group2", 0);
        clientImage = TestEntityCreator.createMockImage("Test Image");
        existingClient = TestEntityCreator.createClientWithImage(existingName, existingGroup, clientImage);
    }

    @Test
    @DisplayName("Group을 포함한 Client 저장 테스트")
    public void saveClientTest() throws Exception {
        // given
        Integer clientCount = 1;
        NewClientRequest changedRequest = createChangeRequest(changedGroupId, changedName);

        when(securityUserGetter.getAuthority()).thenReturn(List.of(Authority.FREE));
        when(groupRepository.findGroupByIdForUpdate(any())).thenReturn(Optional.ofNullable(existingGroup));
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
            Client savedClient = invocation.getArgument(0); // 저장되는 클라이언트 객체
            ReflectionTestUtils.setField(savedClient, "id", clientId);
            return savedClient;
        });

        // when
        ClientOverviewResponse result = clientService.saveClient(changedRequest);

        // then
        assertThat(result.getClientId()).isEqualTo(clientId);
        assertThat(existingGroup.getClientCount()).isEqualTo(clientCount + 1);
    }

    @Test
    @DisplayName("이미지를 제외한 Client 업데이트 테스트")
    public void updateClientTest() throws Exception {
        // given
        Group changedGroup = TestEntityCreator.createGroup(user, changedGroupId, "Changed Group",0);

        NewClientRequest changedRequest = createChangeRequest(changedGroupId, changedName);

        when(securityUserGetter.getAuthority()).thenReturn(List.of(Authority.FREE));
        when(clientRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(existingClient));
        when(groupRepository.findGroupByIdForUpdate(existingClient.getGroup().getId()))
                .thenReturn(Optional.ofNullable(existingGroup));
        when(groupRepository.findGroupByIdForUpdate(changedGroupId))
                .thenReturn(Optional.ofNullable(changedGroup));

        // when
        ClientOverviewResponse response = clientService.updateClientWithoutImage(existingClientId, changedRequest);

        // then
        assertThat(response.getClientName()).isEqualTo(changedName);
        assertThat(response.getGroupInfo().getGroupId()).isEqualTo(changedGroupId);
        assertThat(changedGroup.getClientCount()).isEqualTo(1);
        assertThat(existingGroup.getClientCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Client 이미지 업데이트 테스트")
    public void updateClientImageTest() throws Exception {
        // given
        String updatedImageFilePath = UUID.randomUUID().toString();
        StoredFileDto updatedImageFile = new StoredFileDto(updatedImageFilePath, "ddd");
        NewClientRequest changedRequest = createChangeRequest(existingGroup.getId(), existingName);

        when(clientQueryRepository.findClientWithImage(anyLong()))
                .thenReturn(Optional.ofNullable(existingClient));

        // when
        ClientOverviewResponse response = clientService.updateClientWithNewImage(existingClientId, changedRequest, updatedImageFile);

        // then
        assertThat(clientImage.getIsDeleted()).isTrue(); // 기존 이미지 삭제

        assertThat(response.getGroupInfo().getGroupId()).isEqualTo(existingGroup.getId());
        assertThat(response.getImage().getOriginalFileName()).isSameAs(updatedImageFile.getOriginalFileName());
        assertThat(response.getImage().getFilePath()).isEqualTo(updatedImageFilePath);
    }

    @Test
    @DisplayName("Client Basic Image로 업데이트")
    public void updateClientWithBasicImageTest() throws Exception {
        // given
        NewClientRequest changedRequest = createChangeRequest(existingGroup.getId(), existingName);
        when(clientQueryRepository.findClientWithImage(anyLong()))
                .thenReturn(Optional.ofNullable(existingClient));

        // when
        ClientOverviewResponse response = clientService.updateClientWithBasicImage(existingClientId, changedRequest);

        // then
        assertThat(clientImage.getIsDeleted()).isTrue(); // 기존 이미지 삭제

        assertThat(response.getGroupInfo().getGroupId()).isEqualTo(existingGroup.getId());
        assertThat(response.getImage().getFilePath()).isNull();
    }

    @Test
    @DisplayName("Client Basic Image로 업데이트 + 그룹 변경")
    public void updateClientWithBasicImage2Test() throws Exception {
        // given
        NewClientRequest changedRequest = createChangeRequest(changedGroupId, existingName);

        when(securityUserGetter.getAuthority()).thenReturn(List.of(Authority.FREE));
        when(clientQueryRepository.findClientWithImage(existingClientId))
                .thenReturn(Optional.ofNullable(existingClient));

        when(groupRepository.findGroupByIdForUpdate(existingClient.getGroup().getId()))
                .thenReturn(Optional.ofNullable(existingGroup));
        when(groupRepository.findGroupByIdForUpdate(changedGroupId))
                .thenReturn(Optional.ofNullable(changedGroup));

        int existingGroupClientCount = existingGroup.getClientCount();
        int changedGroupClientCount = changedGroup.getClientCount();

        // when
        ClientOverviewResponse response = clientService.updateClientWithBasicImage(existingClientId, changedRequest);

        // then
        assertThat(clientImage.getIsDeleted()).isTrue();
        assertThat(response.getGroupInfo().getGroupId()).isEqualTo(existingGroup.getId());
        assertThat(response.getImage().getFilePath()).isNull();

        assertThat(existingGroup.getClientCount()).isEqualTo(existingGroupClientCount - 1);
        assertThat(changedGroup.getClientCount()).isEqualTo(changedGroupClientCount + 1);
    }

    @Test
    @DisplayName("Client 삭제 테스트")
    void deleteClientTest() {
        when(clientRepository.findClientWithGroupForUpdate(anyLong()))
                .thenReturn(Optional.ofNullable(existingClient));

        clientService.deleteClient(clientId);

        assertThat(existingGroup.getClientCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Client-Image 삭제 테스트")
    void deleteClientWithImageTest() {
        ClientImage savedImage = existingClient.getClientImage();
        when(clientRepository.findClientWithGroupForUpdate(anyLong()))
                .thenReturn(Optional.ofNullable(existingClient));

        clientService.deleteClient(clientId);

        assertThat(existingGroup.getClientCount()).isEqualTo(0);
        assertThat(savedImage.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("이미지와 함께 Client 저장 테스트")
    void saveClientWithImageTest() {
        NewClientRequest request = createChangeRequest(existingGroup.getId(), "New Name");
        StoredFileDto storedFileDto = new StoredFileDto(clientImage.getSavedPath(), clientImage.getOriginalName());
        int beforeClientCount = existingGroup.getClientCount();

        when(securityUserGetter.getAuthority()).thenReturn(List.of(Authority.FREE));
        when(groupRepository.findGroupByIdForUpdate(existingGroup.getId()))
                .thenReturn(Optional.ofNullable(existingGroup));
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
            Client savedClient = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedClient, "id", clientId);
            return savedClient;
        });

        ClientOverviewResponse result = clientService.saveClientWithImage(request, storedFileDto);

        assertThat(existingGroup.getClientCount()).isEqualTo(beforeClientCount + 1);
        assertThat(result.getClientId()).isEqualTo(clientId);
    }

    @Test
    @DisplayName("단순 이름, 전화번호 저장 테스트")
    void saveSimpleClientListTest() {
        List<SimpleNewClientRequest> clients = createSimpleClientRequestSize3();
        long beforeClientCount = existingGroup.getClientCount();

        when(securityUserGetter.getAuthority()).thenReturn(List.of(Authority.FREE));
        when(groupRepository.findGroupByIdForUpdate(existingGroup.getId()))
                .thenReturn(Optional.ofNullable(existingGroup));
        when(clientRepository.saveAll(any())).thenAnswer(invocation -> {
            List<Client> savedClient = invocation.getArgument(0); // 저장되는 클라이언트 객체
            long tempId = 1;
            for (Client client : savedClient) {
                ReflectionTestUtils.setField(client, "id", tempId++);
            }
            return savedClient;
        });

        SimpleClientBulkRequest bulkRequest = new SimpleClientBulkRequest(existingGroup.getId(), clients);
        clientService.saveSimpleClientList(bulkRequest);

        assertThat(existingGroup.getClientCount()).isEqualTo(beforeClientCount + clients.size());
    }

    @Test
    @DisplayName("파싱한 엑셀 데이터 저장 테스트")
    void saveClientExcelDataTest() {
        List<ParsedClientDto> excelData = createExcelDataSize3();
        long beforeClientCount = existingGroup.getClientCount();
        when(groupRepository.findGroupByIdForUpdate(existingGroup.getId()))
                .thenReturn(Optional.ofNullable(existingGroup));


        clientService.saveClientExcelData(existingGroup.getId(), excelData, List.of(Authority.FREE));

        assertThat(existingGroup.getClientCount()).isEqualTo(beforeClientCount + excelData.size());
    }

    @Test
    @DisplayName("그룹 내의 특정 고객 제거")
    void deleteBulkClientTest() {
        int beforeClientSize = 4;
        Group size4Group = TestEntityCreator.createGroup(user, 1L, "Test Group", beforeClientSize);
        when(groupRepository.findGroupByIdForUpdate(size4Group.getId()))
                .thenReturn(Optional.ofNullable(size4Group));
        List<Long> size3ClientIds = getSize3ClientIds();
        int deletedClientSize = size3ClientIds.size();

        clientService.deleteBulkClient(size4Group.getId(), new ArrayList<>());

        assertThat(existingGroup.getClientCount()).isEqualTo(beforeClientSize - deletedClientSize);
    }

    private static List<Long> getSize3ClientIds() {
        List<Long> size3ClientIds = new ArrayList<>();
        size3ClientIds.add(1L);
        size3ClientIds.add(2L);
        size3ClientIds.add(3L);
        return size3ClientIds;
    }

    private static List<ParsedClientDto> createExcelDataSize3() {
        List<ParsedClientDto> excelData = new ArrayList<>();
        excelData.add(new ParsedClientDto(1, "Test Excel 1", null, null, null, null, true));
        excelData.add(new ParsedClientDto(2, "Test Excel 2", null, null, null, null, true));
        excelData.add(new ParsedClientDto(3, "Test Excel 3", null, null, null, null, true));
        return excelData;
    }


    private static List<SimpleNewClientRequest> createSimpleClientRequestSize3() {
        List<SimpleNewClientRequest> clients = new ArrayList<>();
        clients.add(new SimpleNewClientRequest("Test 1", "010-1111-222"));
        clients.add(new SimpleNewClientRequest("Test 1", null));
        clients.add(new SimpleNewClientRequest("Test 1", "010-1111-222"));
        return clients;
    }

    private static NewClientRequest createChangeRequest(Long changedGroupId, String changedName) {
        NewClientRequest changedRequest = new NewClientRequest();
        changedRequest.setClientName(changedName);
        changedRequest.setGroupId(changedGroupId);
        changedRequest.setLatitude(35d);
        changedRequest.setLongitude(127d);
        return changedRequest;
    }
}