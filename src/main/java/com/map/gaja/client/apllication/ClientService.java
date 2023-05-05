package com.map.gaja.client.apllication;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.client.presentation.dto.request.NewClientBulkRequest;
import com.map.gaja.client.presentation.dto.response.ClientBulkResponse;
import com.map.gaja.client.presentation.dto.response.ClientDeleteResponse;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import com.map.gaja.client.presentation.exception.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientResponse findUser(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));
        return entityToDto(client);
    }

    public ClientBulkResponse saveClients(NewClientBulkRequest requestClients) {
        List<Client> clients = dtoToEntity(requestClients);
        clientRepository.saveAll(clients);
        ClientBulkResponse response = entityToDto(clients);
        return response;
    }

    public ClientDeleteResponse deleteClient(Long clientId) {
        clientRepository.deleteById(clientId);
        return new ClientDeleteResponse(
                HttpStatus.OK.value(),
                clientId,
                "정상적으로 삭제되었습니다."
        );
    }

    private ClientBulkResponse entityToDto(List<Client> clients) {
        List<ClientResponse> responseClients = new ArrayList<>();

        clients.forEach(client -> {
            ClientResponse clientResponse = new ClientResponse();
            clientResponse.setClientId(client.getId());
            clientResponse.setName(client.getName());
            responseClients.add(clientResponse);
        });

        return new ClientBulkResponse(responseClients);
    }

    private List<Client> dtoToEntity(NewClientBulkRequest request) {
        List<Client> clients = new ArrayList<>();
        request.getClients().forEach(client ->
                clients.add(new Client(client.getClientName(), "010-1111-2222", LocalDateTime.now(), null, null, null))
        );
        return clients;
    }

    private ClientResponse entityToDto(Client client) {
        return new ClientResponse(client.getId(), client.getName());
    }

}
