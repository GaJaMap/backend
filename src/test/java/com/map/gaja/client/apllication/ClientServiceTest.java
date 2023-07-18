package com.map.gaja.client.apllication;

import com.map.gaja.client.domain.model.ClientImage;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientAddress;
import com.map.gaja.client.domain.model.ClientLocation;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class ClientServiceTest {
    ClientService clientService;

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private ClientQueryRepository clientQueryRepository;

    @BeforeEach
    void beforeEach() {
        clientService = new ClientService(
                clientRepository,
                groupRepository,
                clientQueryRepository
        );
    }

    @Test
    @DisplayName("Bundle을 포함한 Client 저장 테스트")
    public void saveClientTest() throws Exception {
        // given
        Long clientId = 1L;

        Long groupId = 1L;
        Integer clientCount = 0;
        Group group = createGroup(groupId, clientCount);
        NewClientRequest request = new NewClientRequest();
        request.setGroupId(groupId);

        when(groupRepository.findById(any())).thenReturn(Optional.ofNullable(group));
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
            Client savedClient = invocation.getArgument(0); // 저장되는 클라이언트 객체
            ReflectionTestUtils.setField(savedClient, "id", clientId);
            return savedClient;
        });

        // when
        Long result = clientService.saveClient(request);

        // then
        assertThat(result).isEqualTo(clientId);
        assertThat(group.getClientCount()).isEqualTo(clientCount + 1);
    }

    @Test
    @DisplayName("기존 Client 업데이트 테스트")
    public void updateClientTest() throws Exception {
        // given
        Long groupId = 1L;
        Long changedGroupId = 2L;
        Long existingClientId = 1L;
        String existingName = "test";
        String changedName = "update Test";

        Group existingGroup = createGroup(groupId, 0);

        Group changedGroup = createGroup(changedGroupId, 0);

        Client existingClient = createClient(existingName, existingGroup);

        NewClientRequest changedRequest = new NewClientRequest();
        changedRequest.setClientName(changedName);
        changedRequest.setGroupId(changedGroupId);

        when(clientQueryRepository.findClientWithGroup(anyLong()))
                .thenReturn(Optional.ofNullable(existingClient));
        when(groupRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(changedGroup));

        // when
        clientService.updateClientWithoutImage(existingClientId, changedRequest);

        // then

        assertThat(existingClient.getName()).isEqualTo(changedName);
        assertThat(existingClient.getGroup().getId()).isEqualTo(changedGroupId);
        assertThat(changedGroup.getClientCount()).isEqualTo(1);
        assertThat(existingGroup.getClientCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("기존 Client 이미지 업데이트 테스트")
    public void updateClientImageTest() throws Exception {
        // given
        Long groupId = 1L;
        Long existingClientId = 1L;
        String existingName = "test";

        Group existingGroup = createGroup(groupId, 0);
        Client existingClient = createClient(existingName, existingGroup);
        StoredFileDto updatedImageFile = new StoredFileDto("ccc", "ddd");
        NewClientRequest changedRequest = new NewClientRequest();
        changedRequest.setClientName(existingName);
        changedRequest.setGroupId(existingGroup.getId());

        when(clientQueryRepository.findClientWithGroup(anyLong()))
                .thenReturn(Optional.ofNullable(existingClient));

        // when
        clientService.updateClientWithNewImage(existingClientId, changedRequest, updatedImageFile);

        // then
        assertThat(existingClient.getGroup().getId()).isEqualTo(existingGroup.getId());
        assertThat(existingClient.getClientImage().getOriginalName()).isSameAs(updatedImageFile.getOriginalFileName());
        assertThat(existingClient.getClientImage().getSavedPath()).isSameAs(updatedImageFile.getFilePath());
    }

    @Test
    @DisplayName("Client 삭제 테스트")
    void deleteClientTest() {
        Long groupId = 1L;
        Long clientId = 1L;
        Group group = createGroup(groupId, 0);
        Client client = createClient("testClient", group);

        when(clientQueryRepository.findClientWithGroup(anyLong()))
                .thenReturn(Optional.ofNullable(client));

        clientService.deleteClient(clientId);

        assertThat(group.getClientCount()).isEqualTo(0);
    }

    private static Group createGroup(Long groupId, Integer clientCount) {
        return Group.builder()
                .id(groupId).clientCount(clientCount)
                .build();
    }

    private static Client createClient(String existingName, Group existingGroup) {
        return new Client(
                existingName, "test",
                new ClientAddress(), new ClientLocation(),
                existingGroup, new ClientImage("aaa", "bbb"));
    }
}