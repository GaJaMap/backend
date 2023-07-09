package com.map.gaja.client.apllication;

import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import com.map.gaja.group.domain.exception.GroupNotFoundException;
import com.map.gaja.group.infrastructure.GroupQueryRepository;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import com.map.gaja.client.domain.exception.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.map.gaja.client.apllication.ClientConvertor.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientQueryService {
    private final ClientRepository clientRepository;
    private final ClientQueryRepository clientQueryRepository;
    private final GroupQueryRepository groupQueryRepository;

    public ClientResponse findClient(Long clientId) {
        Client client = clientQueryRepository.findClientWithGroup(clientId)
                .orElseThrow(() -> new ClientNotFoundException());
        return entityToDto(client);
    }

    public ClientListResponse findAllClientsInGroup(Long groupId, @Nullable String wordCond) {
        List<Client> clients = clientQueryRepository.findByGroup_Id(groupId, wordCond);
        return entityToDto(clients);
    }

    public ClientListResponse findClientByConditions(Long groupId, NearbyClientSearchRequest locationSearchCond, String wordCond) {
        List<Long> groupIdList = new ArrayList<>();
        groupIdList.add(groupId);

        List<ClientResponse> clientList = clientQueryRepository.findClientByConditions(groupIdList, locationSearchCond, wordCond);
        return new ClientListResponse(clientList);
    }

    public ClientListResponse findClientByConditions(String loginEmail, NearbyClientSearchRequest locationSearchCond, String wordCond) {
        List<Long> groupIdList = groupQueryRepository.findGroupId(loginEmail);
        if (groupIdList.size() == 0) {
            throw new GroupNotFoundException();
        }

        List<ClientResponse> clientList = clientQueryRepository.findClientByConditions(groupIdList, locationSearchCond, wordCond);
        return new ClientListResponse(clientList);
    }

    public StoredFileDto findClientImage(Long clientId) {
        ClientResponse client = findClient(clientId);
        return client.getImage();
    }

    public ClientListResponse findAllClient(
            String loginEmail,
            @Nullable String nameCond
    ) {
        List<ClientResponse> clientList = clientQueryRepository.findAllClientByEmail(loginEmail, nameCond);
        return new ClientListResponse(clientList);
    }
}
