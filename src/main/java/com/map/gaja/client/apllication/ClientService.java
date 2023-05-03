package com.map.gaja.client.apllication;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.service.ClientDomainService;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.client.presentation.dto.request.NewClientBulkRequest;
import com.map.gaja.client.presentation.dto.response.ClientBulkResponse;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import com.map.gaja.client.presentation.exception.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {
    private final ClientRepository clientRepository;
    private final ClientDomainService clientDomainService;

    public ClientResponse findUser(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));
        return new ClientResponse(client);
    }

    public ClientBulkResponse saveClients(NewClientBulkRequest requestClients) {
        List<Client> clients = clientDomainService.createClients(requestClients);
        clientRepository.saveAll(clients);

        List<ClientResponse> clientResponse = new ArrayList<>();
        clients.forEach(client -> clientResponse.add(new ClientResponse(client)));
        ClientBulkResponse response = new ClientBulkResponse(clientResponse);
        return response;
    }
    
}
