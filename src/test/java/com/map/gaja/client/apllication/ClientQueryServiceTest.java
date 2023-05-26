package com.map.gaja.client.apllication;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientAddress;
import com.map.gaja.client.domain.model.ClientLocation;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import com.map.gaja.client.presentation.exception.ClientNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientQueryServiceTest {
    @Mock
    private ClientRepository repository;

    @InjectMocks
    private ClientQueryService clientQueryService;

    @Test
    public void testFindUser() throws Exception {
        //given
        Long searchId = 1L;
        String searchName = "test";
        Client findClient = mock(Client.class);
        when(repository.findById(searchId)).thenReturn(Optional.ofNullable(findClient));
        when(findClient.getId()).thenReturn(searchId);
        when(findClient.getName()).thenReturn(searchName);
        when(findClient.getAddress()).thenReturn(new ClientAddress());
        when(findClient.getLocation()).thenReturn(new ClientLocation());

        //when
        ClientResponse response = clientQueryService.findUser(searchId);

        //then
        assertThat(response.getClientId()).isEqualTo(searchId);
        assertThat(response.getClientName()).isEqualTo(searchName);
    }

    @Test
    public void testFindUserNotFound() throws Exception {
        //given
        Long searchId = 1L;
        String searchName = "test";
        Client findClient = mock(Client.class);
        when(repository.findById(searchId)).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(()-> clientQueryService.findUser(searchId))
                .isInstanceOf(ClientNotFoundException.class);
    }
}