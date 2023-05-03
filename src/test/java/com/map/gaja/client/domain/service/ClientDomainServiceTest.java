package com.map.gaja.client.domain.service;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.presentation.dto.request.NewClientBulkRequest;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class ClientDomainServiceTest {

    private ClientDomainService domainService = new ClientDomainService();

    @Test
    public void saveTest() throws Exception {
        //given
        NewClientBulkRequest request = new NewClientBulkRequest();
        List<NewClientRequest> requestClients = new ArrayList<>();
        NewClientRequest requestClient1 = new NewClientRequest("Kim", 1L);
        NewClientRequest requestClient2 = new NewClientRequest("Jang", 2L);
        requestClients.add(requestClient1);
        requestClients.add(requestClient2);
        request.setClients(requestClients);

        //when
        List<Client> clients = domainService.createClients(request);

        //then
        Client client1 = clients.get(0);
        Client client2 = clients.get(1);
        assertThat(client1.getName()).isEqualTo(requestClient1.getClientName());
        assertThat(client2.getName()).isEqualTo(requestClient2.getClientName());
    }
}