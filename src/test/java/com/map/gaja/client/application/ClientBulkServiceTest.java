package com.map.gaja.client.application;

import com.map.gaja.TestEntityCreator;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientImage;
import com.map.gaja.client.infrastructure.file.parser.dto.ParsedClientDto;
import com.map.gaja.client.infrastructure.repository.ClientBulkRepository;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.client.presentation.dto.request.simple.SimpleClientBulkRequest;
import com.map.gaja.client.presentation.dto.request.simple.SimpleNewClientRequest;
import com.map.gaja.global.authentication.AuthenticationRepository;
import com.map.gaja.global.event.Events;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.domain.service.IncreasingClientService;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.user.domain.model.Authority;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientBulkServiceTest {

    @Mock ClientRepository clientRepository;
    @Mock GroupRepository groupRepository;
    @Mock ClientBulkRepository clientBulkRepository;
    @Mock UserRepository userRepository;
    @Mock IncreasingClientService increasingClientService;
    @Mock AuthenticationRepository securityUserGetter;
    @InjectMocks
    ClientBulkService clientBulkService;

    @Mock ApplicationEventPublisher publisher;
    @InjectMocks
    Events events;

    Long groupId = 1L;
    String existingName = "test", email = "testEmail";
    User user;
    Group existingGroup, changedGroup;
    Client existingClient;
    ClientImage clientImage;
    @BeforeEach
    void beforeEach() {
        user = TestEntityCreator.createUser(email);
        existingGroup = TestEntityCreator.createGroup(user, groupId, "Test Group1", 1);
        changedGroup = TestEntityCreator.createGroup(user, groupId, "Test Group2", 0);
        clientImage = TestEntityCreator.createClientImage(email);
        existingClient = TestEntityCreator.createClientWithImage(existingName, existingGroup, clientImage, user);
    }

    @Test
    @DisplayName("단순 이름, 전화번호 저장 테스트")
    void saveSimpleClientListTest() {
        List<SimpleNewClientRequest> clients = createSimpleClientRequestSize3();

        when(securityUserGetter.getAuthority()).thenReturn(List.of(Authority.FREE));
        when(userRepository.findByEmail(any())).thenReturn(user);
        when(groupRepository.findGroupByIdForUpdate(existingGroup.getId()))
                .thenReturn(Optional.ofNullable(existingGroup));
        mockClientRepositorySaveAll();

        SimpleClientBulkRequest bulkRequest = new SimpleClientBulkRequest(existingGroup.getId(), clients);
        clientBulkService.saveSimpleClientList(bulkRequest, user.getEmail());

        verify(userRepository).findByEmail(email);
        verify(groupRepository).findGroupByIdForUpdate(groupId);
        verify(increasingClientService).increaseByMany(existingGroup, Authority.FREE, bulkRequest.getClients().size());
        verify(clientRepository).saveAll(any());
    }

    @Test
    @DisplayName("파싱한 엑셀 데이터 저장 테스트")
    void saveClientExcelDataTest() {
        List<ParsedClientDto> excelData = createExcelDataSize3();
        when(groupRepository.findGroupByIdForUpdate(existingGroup.getId()))
                .thenReturn(Optional.ofNullable(existingGroup));
        when(userRepository.findByEmail(any())).thenReturn(user);

        clientBulkService.saveClientExcelData(existingGroup.getId(), excelData, List.of(Authority.FREE), user.getEmail());

        verify(userRepository).findByEmail(email);
        verify(groupRepository).findGroupByIdForUpdate(groupId);
        verify(increasingClientService).increaseByMany(existingGroup, Authority.FREE, excelData.size());
        verify(clientBulkRepository).saveClientWithGroup(eq(existingGroup), any());
    }

    @Test
    @DisplayName("그룹 내의 특정 고객 제거")
    void deleteBulkClientTest() {
        int beforeClientSize = 4;
        Group size4Group = TestEntityCreator.createGroup(user, 1L, "Test Group", beforeClientSize);
        when(groupRepository.findGroupByIdForUpdate(size4Group.getId()))
                .thenReturn(Optional.of(size4Group));
        List<Long> size3ClientIds = getSize3ClientIds();
        int deletedClientSize = size3ClientIds.size();

        clientBulkService.deleteBulkClient(size4Group.getId(), size3ClientIds);

        assertThat(size4Group.getClientCount()).isEqualTo(beforeClientSize - deletedClientSize);
        verify(clientRepository).markClientImageAsDeleted(size3ClientIds);
        verify(clientRepository).deleteClientsInClientIds(size3ClientIds);
    }

    private void mockClientRepositorySaveAll() {
        // saveAll Long 반환시 NPE를 피하기 위한 Mocking
        when(clientRepository.saveAll(any())).thenAnswer(invocation -> {
            List<Client> savedClient = invocation.getArgument(0);
            long tempId = 1;
            for (Client client : savedClient) {
                ReflectionTestUtils.setField(client, "id", tempId++);
            }
            return savedClient;
        });
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

    private static List<Long> getSize3ClientIds() {
        List<Long> size3ClientIds = new ArrayList<>();
        size3ClientIds.add(1L);
        size3ClientIds.add(2L);
        size3ClientIds.add(3L);
        return size3ClientIds;
    }

}