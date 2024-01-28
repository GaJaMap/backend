package com.map.gaja.client.application;

import com.map.gaja.TestEntityCreator;
import com.map.gaja.client.domain.model.ClientImage;
import com.map.gaja.client.presentation.dto.response.ClientOverviewResponse;
import com.map.gaja.global.authentication.AuthenticationRepository;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.domain.service.IncreasingClientService;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class ClientServiceTest {
    @InjectMocks
    ClientService clientService;

    @Mock ClientRepository clientRepository;
    @Mock GroupRepository groupRepository;
    @Mock ClientQueryRepository clientQueryRepository;
    @Mock UserRepository userRepository;
    @Mock IncreasingClientService increasingClientService;
    @Mock AuthenticationRepository securityUserGetter;

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
        user = TestEntityCreator.createUser(email);
        existingGroup = TestEntityCreator.createGroup(user, groupId, "Test Group1", 1);
        changedGroup = TestEntityCreator.createGroup(user, groupId, "Test Group2", 0);
        clientImage = TestEntityCreator.createClientImage(email);
        existingClient = TestEntityCreator.createClientWithImage(existingName, existingGroup, clientImage, user);
    }

    @Test
    @DisplayName("Group을 포함한 Client 저장 테스트")
    public void saveClientTest() throws Exception {
        // given
        NewClientRequest request = createRequest(groupId, changedName);
        when(securityUserGetter.getAuthority()).thenReturn(List.of(Authority.FREE));
        when(userRepository.findByEmail(any())).thenReturn(user);
        when(groupRepository.findGroupByIdForUpdate(any())).thenReturn(Optional.ofNullable(existingGroup));
        mockClientRepoSave();

        // when
        ClientOverviewResponse result = clientService.saveClient(request, user.getEmail());

        // then
        assertThat(result.getClientId()).isEqualTo(clientId);
        verify(userRepository).findByEmail(email);
        verify(groupRepository).findGroupByIdForUpdate(groupId);
        verify(clientRepository).save(any());
        verify(increasingClientService).increaseByOne(existingGroup, Authority.FREE);
    }

    @Test
    @DisplayName("이미지를 제외한 Client 업데이트 테스트")
    public void updateClientTest() throws Exception {
        // given
        Group changedGroup = TestEntityCreator.createGroup(user, changedGroupId, "Changed Group",0);

        NewClientRequest changedRequest = createRequest(changedGroupId, changedName);
        int beforeExistingGroupClientCnt = existingGroup.getClientCount();

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
        verify(increasingClientService).increaseByOne(changedGroup, Authority.FREE);
        assertThat(existingGroup.getClientCount()).isEqualTo(beforeExistingGroupClientCnt - 1);
    }

    @Test
    @DisplayName("Client 이미지 업데이트 테스트")
    public void updateClientImageTest() throws Exception {
        // given
        NewClientRequest changedRequest = createRequest(existingGroup.getId(), existingName);
        String imageName = "testImage.png";
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn(imageName);
        changedRequest.setClientImage(mockFile);

        when(clientQueryRepository.findClientWithImage(anyLong()))
                .thenReturn(Optional.ofNullable(existingClient));

        // when
        ClientOverviewResponse response = clientService.updateClientWithNewImage(existingClientId, changedRequest, email);

        // then
        assertThat(clientImage.getIsDeleted()).isTrue(); // 기존 이미지 삭제

        assertThat(response.getGroupInfo().getGroupId()).isEqualTo(existingGroup.getId());
        assertThat(response.getImage().getOriginalFileName()).isEqualTo(imageName);
        System.out.println(existingGroup.getClientCount());
    }

    @Test
    @DisplayName("Client Basic Image로 업데이트 - 그룹 유지")
    public void updateClientWithBasicImageTest() throws Exception {
        // given
        NewClientRequest request = createRequest(existingGroup.getId(), existingName);
        request.setIsBasicImage(true);
        when(clientQueryRepository.findClientWithImage(anyLong()))
                .thenReturn(Optional.ofNullable(existingClient));

        // when
        ClientOverviewResponse response = clientService.updateClientWithBasicImage(existingClientId, request);

        // then
        verify(increasingClientService, times(0)).increaseByOne(any(), any());
        assertThat(clientImage.getIsDeleted()).isTrue(); // 기존 이미지 삭제
        assertThat(response.getGroupInfo().getGroupId()).isEqualTo(existingGroup.getId());
        assertThat(response.getImage().getFilePath()).isNull();
    }

    @Test
    @DisplayName("Client Basic Image로 업데이트 + 그룹 변경")
    public void updateClientWithBasicImage2Test() throws Exception {
        // given
        NewClientRequest changedRequest = createRequest(changedGroupId, existingName);

        when(securityUserGetter.getAuthority()).thenReturn(List.of(Authority.FREE));
        when(clientQueryRepository.findClientWithImage(existingClientId))
                .thenReturn(Optional.ofNullable(existingClient));

        when(groupRepository.findGroupByIdForUpdate(groupId))
                .thenReturn(Optional.ofNullable(existingGroup));
        when(groupRepository.findGroupByIdForUpdate(changedGroupId))
                .thenReturn(Optional.ofNullable(changedGroup));

        int beforeExistingGroupClientCount = existingGroup.getClientCount();

        // when
        ClientOverviewResponse response = clientService.updateClientWithBasicImage(existingClientId, changedRequest);

        // then
        assertThat(clientImage.getIsDeleted()).isTrue();
        assertThat(response.getGroupInfo().getGroupId()).isEqualTo(existingGroup.getId());
        assertThat(response.getImage().getFilePath()).isNull();
        assertThat(existingGroup.getClientCount()).isEqualTo(beforeExistingGroupClientCount - 1);
        verify(increasingClientService, times(1)).increaseByOne(changedGroup, Authority.FREE);
    }

    @Test
    @DisplayName("Client 삭제 테스트")
    void deleteClientTest() {
        int beforeGroupClientCnt = existingGroup.getClientCount();
        ClientImage deletedImage = existingClient.getClientImage();
        when(clientRepository.findClientWithGroupForUpdate(existingClientId))
                .thenReturn(Optional.ofNullable(existingClient));

        clientService.deleteClient(existingClientId);

        verify(clientRepository).delete(existingClient);
        assertThat(deletedImage.getIsDeleted()).isTrue();
        assertThat(existingClient.getGroup().getClientCount()).isEqualTo(beforeGroupClientCnt - 1);
    }

    @Test
    @DisplayName("이미지와 함께 Client 저장 테스트")
    void saveClientWithImageTest() {
        String changedName = "New Name";
        String extension = "png";
        String changedImageName = "testImage." + extension;
        NewClientRequest request = createRequest(existingGroup.getId(), changedName);
        request.setClientImage(TestEntityCreator.createMockFile(changedImageName));

        when(securityUserGetter.getAuthority()).thenReturn(List.of(Authority.FREE));
        when(groupRepository.findGroupByIdForUpdate(existingGroup.getId()))
                .thenReturn(Optional.ofNullable(existingGroup));
        mockClientRepoSave();
        when(userRepository.findByEmail(any())).thenReturn(user);

        ClientOverviewResponse result = clientService.saveClientWithImage(request,email);

        verify(clientRepository).save(any());
        verify(userRepository).findByEmail(email);
        verify(groupRepository).findGroupByIdForUpdate(groupId);
        verify(increasingClientService).increaseByOne(existingGroup, Authority.FREE);
        assertThat(result.getClientId()).isEqualTo(clientId);
        assertThat(result.getImage().getFilePath()).contains(extension);
    }

    private static NewClientRequest createRequest(Long groupId, String name) {
        NewClientRequest changedRequest = new NewClientRequest();
        changedRequest.setClientName(name);
        changedRequest.setGroupId(groupId);
        changedRequest.setLatitude(35d);
        changedRequest.setLongitude(127d);

        return changedRequest;
    }

    private void mockClientRepoSave() {
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
            Client savedClient = invocation.getArgument(0); // 저장되는 클라이언트 객체
            ReflectionTestUtils.setField(savedClient, "id", clientId);
            return savedClient;
        });
    }
}