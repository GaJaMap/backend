package com.map.gaja.client.infrastructure.repository;

import com.map.gaja.client.domain.model.Client;

import java.util.List;

public interface ClientRepositoryCustom {
    List<Client> mockFindClientByCondition(String name);
}
