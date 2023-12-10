package com.map.gaja.client.application;

import com.map.gaja.client.presentation.dto.response.ClientDetailResponse;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import com.map.gaja.group.domain.exception.GroupNotFoundException;
import com.map.gaja.group.infrastructure.GroupQueryRepository;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.client.presentation.dto.response.ClientOverviewResponse;
import com.map.gaja.client.domain.exception.ClientNotFoundException;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.map.gaja.client.application.ClientConvertor.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientQueryService {
    private final ClientQueryRepository clientQueryRepository;
    private final GroupQueryRepository groupQueryRepository;
    private final UserRepository userRepository;

    public ClientDetailResponse findClient(Long clientId) {
        Client client = clientQueryRepository.findClientWithGroup(clientId)
                .orElseThrow(() -> new ClientNotFoundException());
        return entityToDetailDto(client);
    }

    public ClientListResponse findAllClientsInGroup(Long groupId, @Nullable String wordCond) {
        List<Client> clients = clientQueryRepository.findByGroup_Id(groupId, wordCond);
        return entityToDto(clients);
    }

    public ClientListResponse findClientByConditions(Long groupId, NearbyClientSearchRequest locationSearchCond, String wordCond) {
        List<Long> groupIdList = new ArrayList<>();
        groupIdList.add(groupId);

        List<ClientOverviewResponse> clientList = clientQueryRepository.findClientByConditions(groupIdList, locationSearchCond, wordCond);
        return new ClientListResponse(clientList);
    }

    public StoredFileDto findClientImage(Long clientId) {
        ClientDetailResponse client = findClient(clientId);
        return client.getImage();
    }

    public String findImageFilePath(Long clientId) {
        return clientQueryRepository.findClientImageFilePath(clientId);
    }

    @Transactional
    public ClientListResponse findAllClient(
            String loginEmail,
            @Nullable String nameCond
    ) {
        accessAllClient(loginEmail);

        List<ClientOverviewResponse> clientList = clientQueryRepository.findActiveClientByEmail(loginEmail, nameCond);
        return new ClientListResponse(clientList);
    }

    @Transactional
    public ClientListResponse findClientByConditions(String loginEmail, NearbyClientSearchRequest locationSearchCond, String wordCond) {
        accessAllClient(loginEmail);

        List<Long> groupIdList = groupQueryRepository.findActiveGroupId(loginEmail);
        if (groupIdList.size() == 0) {
            throw new GroupNotFoundException();
        }

        List<ClientOverviewResponse> clientList = clientQueryRepository.findClientByConditions(groupIdList, locationSearchCond, wordCond);
        return new ClientListResponse(clientList);
    }

    private void accessAllClient(String loginEmail) {
        User user = userRepository.findByEmail(loginEmail);
        user.accessGroup(null);
    }
}
