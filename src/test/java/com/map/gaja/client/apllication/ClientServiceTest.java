package com.map.gaja.client.apllication;

import com.map.gaja.bundle.domain.model.Bundle;
import com.map.gaja.bundle.infrastructure.BundleRepository;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.response.CreatedClientResponse;
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
        Long bundleId = 1L;
        Long clientId = 1L;
        Integer clientCount = 0;

        Bundle bundle = Bundle.builder()
                .id(bundleId).clientCount(clientCount)
                .build();
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

}