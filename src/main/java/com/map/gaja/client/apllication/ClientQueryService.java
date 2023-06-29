package com.map.gaja.client.apllication;

import com.map.gaja.bundle.domain.exception.BundleNotFoundException;
import com.map.gaja.bundle.infrastructure.BundleQueryRepository;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import com.map.gaja.client.presentation.exception.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
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
    private final BundleQueryRepository bundleQueryRepository;

    public ClientResponse findClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));
        return entityToDto(client);
    }

    public ClientListResponse findAllClientsInBundle(Long bundleId) {
        List<Client> clients = clientRepository.findByBundle_Id(bundleId);
        return entityToDto(clients);
    }

    public ClientListResponse findClientByConditions(Long bundleId, NearbyClientSearchRequest locationSearchCond, String wordCond) {
        List<Long> bundleIdList = new ArrayList<>();
        bundleIdList.add(bundleId);

        List<ClientResponse> clientList = clientQueryRepository.findClientByConditions(bundleIdList, locationSearchCond, wordCond);
        return new ClientListResponse(clientList);
    }

    public ClientListResponse findClientByConditions(String loginEmail, NearbyClientSearchRequest locationSearchCond, String wordCond) {
        List<Long> bundleIdList = bundleQueryRepository.findBundleId(loginEmail);
        if (bundleIdList.size() == 0) {
            throw new BundleNotFoundException();
        }

        List<ClientResponse> clientList = clientQueryRepository.findClientByConditions(bundleIdList, locationSearchCond, wordCond);
        return new ClientListResponse(clientList);
    }

}
