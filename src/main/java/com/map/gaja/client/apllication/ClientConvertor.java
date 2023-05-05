package com.map.gaja.client.apllication;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.presentation.dto.request.NewClientBulkRequest;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.client.presentation.dto.response.ClientResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Request -> Entity 또는 Entity -> Response 컨버터
 */
public class ClientConvertor {
    protected static ClientListResponse entityToDto(List<Client> clients) {
        List<ClientResponse> responseClients = new ArrayList<>();

        clients.forEach(client -> {
            ClientResponse clientResponse = new ClientResponse();
            clientResponse.setClientId(client.getId());
            clientResponse.setName(client.getName());
            responseClients.add(clientResponse);
        });

        return new ClientListResponse(responseClients);
    }

    protected static List<Client> dtoToEntity(NewClientBulkRequest request) {
        List<Client> clients = new ArrayList<>();
        request.getClients().forEach(client ->
                clients.add(new Client(client.getClientName(), "010-1111-2222", LocalDateTime.now(), null, null, null))
        );
        return clients;
    }

    protected static ClientResponse entityToDto(Client client) {
        return new ClientResponse(client.getId(), client.getName());
    }
}
