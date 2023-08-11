package com.map.gaja.client.apllication;

import com.map.gaja.client.domain.model.ClientImage;
import com.map.gaja.client.infrastructure.repository.ClientBulkRepository;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.domain.service.IncreasingClientService;
import com.map.gaja.group.infrastructure.GroupQueryRepository;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientAddress;
import com.map.gaja.client.domain.model.ClientLocation;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.user.domain.model.User;
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
    private ClientBulkRepository clientBulkRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private ClientQueryRepository clientQueryRepository;
    @Mock
    GroupQueryRepository groupQueryRepository;
    IncreasingClientService increasingClientService = new IncreasingClientService();

    Long clientId = 1L;
    Long groupId = 1L;
    Long changedGroupId = 2L;
    Long existingClientId = 1L;
    String existingName = "test";
    String changedName = "update Test";
    String email = "testEmail";

    User user;
    Group group;
    Client existingClient;
    ClientImage clientImage;

    @BeforeEach
    void beforeEach() {
        clientService = new ClientService(
                clientRepository,
                clientBulkRepository,
                groupRepository,
                groupQueryRepository,
                clientQueryRepository,
                increasingClientService
        );

        user = new User(email);
        group = createGroup(user, groupId, 1);
        clientImage = createClientImageImage();
        existingClient = createClientWithImage(existingName, group, clientImage);
    }

    @Test
    @DisplayName("Group을 포함한 Client 저장 테스트")
    public void saveClientTest() throws Exception {
        // given
        Integer clientCount = 1;
        NewClientRequest changedRequest = createChangeRequest(changedGroupId, changedName);

        when(groupQueryRepository.findGroupWithUser(any())).thenReturn(Optional.ofNullable(group));
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
            Client savedClient = invocation.getArgument(0); // 저장되는 클라이언트 객체
            ReflectionTestUtils.setField(savedClient, "id", clientId);
            return savedClient;
        });

        // when
        Long result = clientService.saveClient(changedRequest);

        // then
        assertThat(result).isEqualTo(clientId);
        assertThat(group.getClientCount()).isEqualTo(clientCount + 1);
    }

    @Test
    @DisplayName("이미지를 제외한 Client 업데이트 테스트")
    public void updateClientTest() throws Exception {
        // given
        Group changedGroup = createGroup(user, changedGroupId, 0);
        NewClientRequest changedRequest = createChangeRequest(changedGroupId, changedName);

        when(clientQueryRepository.findClientWithGroup(anyLong()))
                .thenReturn(Optional.ofNullable(existingClient));
        when(groupQueryRepository.findGroupWithUser(anyLong()))
                .thenReturn(Optional.ofNullable(changedGroup));

        // when
        clientService.updateClientWithoutImage(existingClientId, changedRequest);

        // then
        assertThat(existingClient.getName()).isEqualTo(changedName);
        assertThat(existingClient.getGroup().getId()).isEqualTo(changedGroupId);
        assertThat(changedGroup.getClientCount()).isEqualTo(1);
        assertThat(group.getClientCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Client 이미지 업데이트 테스트")
    public void updateClientImageTest() throws Exception {
        // given
        StoredFileDto updatedImageFile = new StoredFileDto("ccc", "ddd");
        NewClientRequest changedRequest = createChangeRequest(group.getId(), existingName);

        when(clientQueryRepository.findClientWithGroup(anyLong()))
                .thenReturn(Optional.ofNullable(existingClient));

        // when
        clientService.updateClientWithNewImage(existingClientId, changedRequest, updatedImageFile);

        // then
        assertThat(existingClient.getGroup().getId()).isEqualTo(group.getId());
        assertThat(existingClient.getClientImage().getOriginalName()).isSameAs(updatedImageFile.getOriginalFileName());
        assertThat(existingClient.getClientImage().getSavedPath()).isSameAs(updatedImageFile.getFilePath());
        assertThat(clientImage.getIsDeleted()).isTrue();
        assertThat(existingClient.getClientImage().getIsDeleted()).isFalse();
    }

    @Test
    @DisplayName("Client Basic Image로 업데이트")
    public void updateClientWithBasicImageTest() throws Exception {
        // given
        NewClientRequest changedRequest = createChangeRequest(group.getId(), existingName);

        when(clientQueryRepository.findClientWithGroup(anyLong()))
                .thenReturn(Optional.ofNullable(existingClient));

        // when
        clientService.updateClientWithBasicImage(existingClientId, changedRequest);

        // then
        assertThat(existingClient.getGroup().getId()).isEqualTo(group.getId());
        assertThat(clientImage.getIsDeleted()).isTrue();
        assertThat(existingClient.getClientImage()).isNull();
    }

    private static ClientImage createClientImageImage() {
        return new ClientImage("aaa", "bbb");
    }

    @Test
    @DisplayName("Client 삭제 테스트")
    void deleteClientTest() {
        when(clientQueryRepository.findClientWithGroup(anyLong()))
                .thenReturn(Optional.ofNullable(existingClient));

        clientService.deleteClient(clientId);

        assertThat(group.getClientCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Client-Image 삭제 테스트")
    void deleteClientWithImageTest() {
        ClientImage image = createClientImageImage();
        Client client = createClientWithImage(existingName, group, image);

        when(clientQueryRepository.findClientWithGroup(anyLong()))
                .thenReturn(Optional.ofNullable(client));

        clientService.deleteClient(clientId);

        assertThat(group.getClientCount()).isEqualTo(0);
        assertThat(image.getIsDeleted()).isTrue();
    }

    private static Group createGroup(User user, Long groupId, Integer clientCount) {
        return Group.builder()
                .user(user)
                .id(groupId).clientCount(clientCount)
                .build();
    }

    private static Client createClient(String existingName, Group existingGroup) {
        return new Client(
                existingName, "test",
                new ClientAddress(), new ClientLocation(),
                existingGroup, createClientImageImage());
    }

    private static Client createClientWithImage(String existingName, Group existingGroup, ClientImage existingImage) {
        return new Client(
                existingName, "test",
                new ClientAddress(), new ClientLocation(),
                existingGroup, existingImage);
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