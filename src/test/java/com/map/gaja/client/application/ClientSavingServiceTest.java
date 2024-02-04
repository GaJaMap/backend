package com.map.gaja.client.application;

import com.map.gaja.TestEntityCreator;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientImage;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.response.ClientOverviewResponse;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientSavingServiceTest {

    @Mock ClientRepository clientRepository;
    @Mock GroupRepository groupRepository;
    @Mock IncreasingClientService increasingClientService;
    @Mock AuthenticationRepository securityUserGetter;
    @Mock UserRepository userRepository;
    @InjectMocks ClientSavingService clientSavingService;

    @Mock ApplicationEventPublisher publisher;
    @InjectMocks
    Events events;


    Long clientId = 1L, groupId = 1L;
    String existingName = "test",
            changedName = "update Test",
            email = "testEmail";

    User user;
    Group existingGroup, changedGroup;
    Client existingClient;
    ClientImage clientImage;

    @BeforeEach
    void beforeEach() {
        System.out.println(securityUserGetter);
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
        when(groupRepository.findByIdAndUserEmail(groupId, user.getEmail()))
                .thenReturn(Optional.ofNullable(existingGroup));
        mockClientRepoSave();

        // when
        ClientOverviewResponse result = clientSavingService.saveClient(request, user.getEmail());

        // then
        assertThat(result.getClientId()).isEqualTo(clientId);
        verify(userRepository).findByEmail(email);
        verify(groupRepository).findByIdAndUserEmail(groupId, user.getEmail());
        verify(clientRepository).save(any());
        verify(increasingClientService).increaseByOne(existingGroup, Authority.FREE);
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
        when(groupRepository.findByIdAndUserEmail(groupId, email))
                .thenReturn(Optional.ofNullable(existingGroup));
        mockClientRepoSave();
        when(userRepository.findByEmail(any())).thenReturn(user);

        ClientOverviewResponse result = clientSavingService.saveClientWithImage(request,email);

        verify(clientRepository).save(any());
        verify(userRepository).findByEmail(email);
        verify(groupRepository).findByIdAndUserEmail(groupId, email);
        verify(increasingClientService).increaseByOne(existingGroup, Authority.FREE);
        assertThat(result.getClientId()).isEqualTo(clientId);
        assertThat(result.getImage().getFilePath()).contains(extension);
    }

    private void mockClientRepoSave() {
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
            Client savedClient = invocation.getArgument(0); // 저장되는 클라이언트 객체
            ReflectionTestUtils.setField(savedClient, "id", clientId);
            return savedClient;
        });
    }

    private static NewClientRequest createRequest(Long groupId, String name) {
        NewClientRequest changedRequest = new NewClientRequest();
        changedRequest.setClientName(name);
        changedRequest.setGroupId(groupId);
        changedRequest.setLatitude(35d);
        changedRequest.setLongitude(127d);

        return changedRequest;
    }

}