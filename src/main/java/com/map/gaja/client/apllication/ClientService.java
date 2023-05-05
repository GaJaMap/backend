package com.map.gaja.client.apllication;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.client.presentation.dto.request.NewClientBulkRequest;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.client.presentation.dto.response.ClientDeleteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.map.gaja.client.apllication.ClientConvertor.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientListResponse saveClients(NewClientBulkRequest requestClients) {
        List<Client> clients = dtoToEntity(requestClients);
        clientRepository.saveAll(clients);
        ClientListResponse response = entityToDto(clients);
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



}
