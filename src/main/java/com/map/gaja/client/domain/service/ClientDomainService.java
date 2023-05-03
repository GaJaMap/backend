package com.map.gaja.client.domain.service;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.presentation.dto.request.NewClientBulkRequest;

import java.util.ArrayList;
import java.util.List;

public class ClientDomainService {
    public List<Client> createClients(NewClientBulkRequest request) {
        List<Client> clients = new ArrayList<>();
        request.getClients().forEach(
                requestClient -> clients.add(new Client(requestClient.getClientName()))
        );
        return clients;
    }
}
