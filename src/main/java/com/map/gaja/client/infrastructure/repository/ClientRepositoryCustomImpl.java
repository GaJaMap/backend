package com.map.gaja.client.infrastructure.repository;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import com.map.gaja.client.presentation.dto.subdto.AddressDto;
import com.map.gaja.client.presentation.dto.subdto.LocationDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static com.map.gaja.client.domain.model.QClient.*;

/**
 * QueryDSL없이 임시로 만듦.
 */
@RequiredArgsConstructor
public class ClientRepositoryCustomImpl implements ClientRepositoryCustom {
    private final EntityManager em;
    private final JPAQueryFactory query;

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

    public Page<ClientResponse> findClientByConditions(NearbyClientSearchRequest locationCond, String keyword, Pageable pageable) {
        List<ClientResponse> result = query.select(
                        Projections.constructor(ClientResponse.class,
                                client.id,
                                Expressions.asNumber(1L), // 임시 번들 데이터
                                client.name,
                                client.phoneNumber,
                                Projections.constructor(
                                        AddressDto.class,
                                        client.address.province, client.address.city,
                                        client.address.district, client.address.detail
                                ),
                                Projections.constructor(
                                        LocationDto.class,
                                        client.location.latitude,
                                        client.location.longitude
                                )
                        )
                )
                .from(client)
                .where(allContains(keyword), nearByUser(locationCond))
                .orderBy(client.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = query.select(client.count())
                .from(client)
                .where(allContains(keyword), nearByUser(locationCond))
                .fetchOne();

        return new PageImpl<>(result, pageable, total);
    }

    private BooleanExpression nearByUser(NearbyClientSearchRequest locationCond) {
        if(isLocationCondEmpty(locationCond)) {
            return null;
        }

        // 반경 검색 조건은 일단 null
        return null;
    }

    private boolean isLocationCondEmpty(NearbyClientSearchRequest locationCond) {
        return locationCond == null
                || locationCond.getLocation() == null
                || locationCond.getRadius() == null;
    }

    private BooleanExpression allContains(String keywordCond) {
        return nameContains(keywordCond)
                .or(addressContains(keywordCond))
                .or(phoneNumberContains(keywordCond));
    }

    private BooleanExpression phoneNumberContains(String phoneNumberCond) {
        return phoneNumberCond != null ? client.phoneNumber.contains(phoneNumberCond) : null;
    }

    private BooleanExpression nameContains(String nameCond) {
        return nameCond != null ? client.name.contains(nameCond) : null;
    }

    private BooleanExpression addressContains(String addressCond) {
        return addressCond != null ? client.address.city.contains(addressCond)
                .or(client.address.province.contains(addressCond))
                .or(client.address.district.contains(addressCond))
                .or(client.address.detail.contains(addressCond)) : null;
    }
}
