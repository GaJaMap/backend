package com.map.gaja.client.apllication;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import com.map.gaja.client.presentation.exception.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.map.gaja.client.apllication.ClientConvertor.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientQueryService {
    private final ClientRepository clientRepository;

    public ClientResponse findClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));
        return entityToDto(client);
    }

    public ClientListResponse findClient() {
        List<Client> clients = clientRepository.findAll();
        return entityToDto(clients);
    }

    public Page<ClientResponse> findClientByConditions(NearbyClientSearchRequest locationSearchCond, String wordCond, Pageable pageable) {
        return clientRepository.findClientByConditions(locationSearchCond, wordCond, pageable);
    }

}
