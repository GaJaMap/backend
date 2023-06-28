package com.map.gaja.client.apllication;

import com.map.gaja.bundle.domain.exception.BundleNotFoundException;
import com.map.gaja.bundle.domain.model.Bundle;
import com.map.gaja.bundle.infrastructure.BundleQueryRepository;
import com.map.gaja.bundle.infrastructure.BundleRepository;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientAddress;
import com.map.gaja.client.domain.model.ClientLocation;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.client.presentation.dto.ClientAccessCheckDto;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import com.map.gaja.client.presentation.dto.response.CreatedClientResponse;
import com.map.gaja.client.presentation.exception.ClientNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class ClientServiceTest {
    ClientService clientService;

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private BundleRepository bundleRepository;
    @Mock
    private ClientQueryRepository clientQueryRepository;

    @BeforeEach
    void beforeEach() {
        clientService = new ClientService(
                clientRepository,
                bundleRepository,
                clientQueryRepository,
                new ArrayList<>()
        );
    }

    @Test
    @DisplayName("Bundle을 포함한 Client 저장 테스트")
    public void saveClientTest() throws Exception {
        // given
        Long clientId = 1L;

        Long bundleId = 1L;
        Integer clientCount = 0;
        Bundle bundle = createBundle(bundleId, clientCount);
        NewClientRequest request = new NewClientRequest();
        request.setBundleId(bundleId);

        when(bundleRepository.findById(any())).thenReturn(Optional.ofNullable(bundle));
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
            Client savedClient = invocation.getArgument(0); // 저장되는 클라이언트 객체
            ReflectionTestUtils.setField(savedClient, "id", clientId);
            return savedClient;
        });

        // when
        CreatedClientResponse response = clientService.saveClient(request);

        // then
        assertThat(response.getClientId()).isEqualTo(clientId);
        assertThat(bundle.getClientCount()).isEqualTo(clientCount + 1);
    }

    @Test
    @DisplayName("기존 Client 업데이트 테스트")
    public void updateClientTest() throws Exception {
        // given
        Long bundleId = 1L;
        Long changedBundleId = 2L;
        Long existingClientId = 1L;
        String existingName = "test";
        String changedName = "update Test";

        Bundle existingBundle = createBundle(bundleId, 0);

        Bundle changedBundle = createBundle(changedBundleId, 0);

        Client existngClient = createClient(existingName, existingBundle);

        NewClientRequest changedRequest = new NewClientRequest();
        changedRequest.setClientName(changedName);
        changedRequest.setBundleId(changedBundleId);

        when(clientQueryRepository.findClientWithBundle(anyLong()))
                .thenReturn(Optional.ofNullable(existngClient));
        when(bundleRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(changedBundle));

        // when
        ClientResponse response = clientService.changeClient(existingClientId, changedRequest);

        // then
        assertThat(response.getClientName()).isEqualTo(changedName);
        assertThat(response.getBundleId()).isEqualTo(changedBundleId);
        assertThat(changedBundle.getClientCount()).isEqualTo(1);
        assertThat(existingBundle.getClientCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Client 삭제 테스트")
    void deleteClientTest() {
        Long bundleId = 1L;
        Long clientId = 1L;
        Bundle bundle = createBundle(bundleId, 0);
        Client client = createClient("testClient", bundle);

        when(clientQueryRepository.findClientWithBundle(anyLong()))
                .thenReturn(Optional.ofNullable(client));

        clientService.deleteClient(clientId);

        assertThat(bundle.getClientCount()).isEqualTo(0);
    }

    private static Bundle createBundle(Long bundleId, Integer clientCount) {
        return Bundle.builder()
                .id(bundleId).clientCount(clientCount)
                .build();
    }

    private static Client createClient(String existingName, Bundle existingBundle) {
        return new Client(
                existingName, "test",
                new ClientAddress(), new ClientLocation(),
                existingBundle, null);
    }
}