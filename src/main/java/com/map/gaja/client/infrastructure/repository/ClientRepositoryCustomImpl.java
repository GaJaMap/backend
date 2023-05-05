package com.map.gaja.client.infrastructure.repository;

import com.map.gaja.client.domain.model.Client;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * QueryDSL없이 임시로 만듦.
 */
@RequiredArgsConstructor
public class ClientRepositoryCustomImpl implements ClientRepositoryCustom {
    private final EntityManager em;

    public List<Client> mockFindClientByCondition(String name) {
        List<Client> list = em.createQuery("Select c From Client c Where c.name like '%:name%'", Client.class)
                .setParameter("name", name)
                .getResultList();
        return list;
    }
}
