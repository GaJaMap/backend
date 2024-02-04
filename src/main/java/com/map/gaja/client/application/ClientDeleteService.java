package com.map.gaja.client.application;

import com.map.gaja.client.domain.exception.ClientNotFoundException;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.group.domain.model.Group;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientDeleteService {
    private final ClientRepository clientRepository;

    public void deleteClient(long clientId) {
        Client deletedClient = clientRepository.findClientWithGroupForUpdate(clientId)
                .orElseThrow(() -> new ClientNotFoundException());
        Group group = deletedClient.getGroup();

        group.decreaseClientCount(1);
        deletedClient.removeImage();

        clientRepository.delete(deletedClient);
    }
}
