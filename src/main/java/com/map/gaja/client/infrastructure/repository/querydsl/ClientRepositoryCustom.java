package com.map.gaja.client.infrastructure.repository.querydsl;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClientRepositoryCustom {
    List<Client> mockFindClientByCondition(String name);
    Page<ClientResponse> findClientByConditions(NearbyClientSearchRequest request, String keyword, Pageable pageable);
    List<Client> findClientsByLocation(NearbyClientSearchRequest request);
}
