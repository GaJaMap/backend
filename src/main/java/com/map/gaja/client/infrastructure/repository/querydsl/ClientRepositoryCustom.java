package com.map.gaja.client.infrastructure.repository.querydsl;

import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ClientRepositoryCustom {
    Slice<ClientResponse> findClientByConditions(NearbyClientSearchRequest locationSearchCond, String wordCond, Pageable pageable);
}
