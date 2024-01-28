package com.map.gaja.client.application;

import com.map.gaja.TestEntityCreator;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientImage;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ClientDeleteServiceTest {
    @Mock ClientRepository clientRepository;
    @InjectMocks ClientDeleteService clientDeleteService;

    Long groupId = 1L, existingClientId = 1L;
    String existingName = "test",
            email = "testEmail";

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
    @DisplayName("Client 삭제 테스트")
    void deleteClientTest() {
        int beforeGroupClientCnt = existingGroup.getClientCount();
        ClientImage deletedImage = existingClient.getClientImage();
        when(clientRepository.findClientWithGroupForUpdate(existingClientId))
                .thenReturn(Optional.ofNullable(existingClient));

        clientDeleteService.deleteClient(existingClientId);

        verify(clientRepository).delete(existingClient);
        assertThat(deletedImage.getIsDeleted()).isTrue();
        assertThat(existingClient.getGroup().getClientCount()).isEqualTo(beforeGroupClientCnt - 1);
    }
}