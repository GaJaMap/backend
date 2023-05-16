package com.map.gaja.client.infrastructure.repository;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
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
        List<Client> list = em.createQuery("Select c From Client c Where c.name like :name", Client.class)
                .setParameter("name", "%"+name+"%")
                .getResultList();
        return list;
    }

    public List<Client> findClientsByLocation(NearbyClientSearchRequest request) {
        List<Client> list = em.createQuery("SELECT c FROM Client c " +
                        "WHERE c.location.latitude BETWEEN (:lat - :radius) AND (:lat + :radius) " +
                        "AND c.location.longitude BETWEEN (:lng - :radius) AND (:lng + :radius) " +
                        "AND (6371000 * SQRT(POW(c.location.latitude - :lat, 2) + POW(c.location.longitude - :lng, 2))) <= :radius", Client.class)
                .setParameter("lat", request.getLocation().getLatitude())
                .setParameter("lng", request.getLocation().getLongitude())
                .setParameter("radius", request.getRadius())
                .getResultList();
        return list;
    }
}
