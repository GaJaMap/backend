package com.map.gaja.client.infrastructure.repository.querydsl;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.infrastructure.repository.querydsl.sql.NativeSQLCreator;
import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import com.map.gaja.client.presentation.dto.subdto.AddressDto;
import com.map.gaja.client.presentation.dto.subdto.LocationDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static com.map.gaja.client.domain.model.QClient.*;

@RequiredArgsConstructor
public class ClientRepositoryCustomImpl implements ClientRepositoryCustom {
    private final EntityManager em;
    private final JPAQueryFactory query;
    private final NativeSQLCreator nativeSQLCreator;

    public List<Client> mockFindClientByCondition(String nameCond) {
        List<Client> list = query.selectFrom(client)
                .where(client.name.containsIgnoreCase(nameCond))
                .fetch();
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

    public Page<ClientResponse> findClientByConditions(NearbyClientSearchRequest locationSearchCond, String wordCond, Pageable pageable) {
        List<ClientResponse> result = query.select(
                        Projections.constructor(ClientResponse.class,
                                client.id,
                                Expressions.asNumber(1L), // 임시 번들 데이터
                                client.name,
                                client.phoneNumber,
                                getAddressDto(),
                                getLocationDto(),
                                Expressions.asNumber(1d) // 임시 distance
                        )
                )
                .from(client)
                .where(allContains(wordCond), isClientInRadius(locationSearchCond))
                .orderBy(distanceAsc(locationSearchCond), client.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = query.select(client.count())
                .from(client)
                .where(allContains(wordCond), isClientInRadius(locationSearchCond))
                .fetchOne();

        return new PageImpl<>(result, pageable, total);
    }

    private static ConstructorExpression<LocationDto> getLocationDto() {
        return Projections.constructor(
                LocationDto.class,
                client.location.latitude,
                client.location.longitude
        );
    }

    private static ConstructorExpression<AddressDto> getAddressDto() {
        return Projections.constructor(
                AddressDto.class,
                client.address.province, client.address.city,
                client.address.district, client.address.detail
        );
    }

    private OrderSpecifier<?> distanceAsc(NearbyClientSearchRequest locationCond) {
        if(locationCond == null || isCurrentLocationEmpty(locationCond.getLocation())) {
            return new OrderSpecifier(Order.ASC, NullExpression.DEFAULT, OrderSpecifier.NullHandling.Default);
        }

        return getCalcDistanceNativeSQL(locationCond.getLocation()).asc();
    }

    private BooleanExpression isClientInRadius(NearbyClientSearchRequest locationSearchCond) {
        if(isLocationSearchCondEmpty(locationSearchCond)) {
            return null;
        }

        LocationDto currentLocation = locationSearchCond.getLocation();
        return getCalcDistanceNativeSQL(currentLocation)
                .loe(locationSearchCond.getRadius());
    }

    private NumberExpression<Double> getCalcDistanceNativeSQL(LocationDto currentLocation) {
        return nativeSQLCreator.createCalcDistanceSQL(
                currentLocation.getLongitude(), currentLocation.getLatitude(),
                client.location.longitude, client.location.latitude
        );
    }

    private boolean isLocationSearchCondEmpty(NearbyClientSearchRequest locationSearchCond) {
        return locationSearchCond == null
                || isCurrentLocationEmpty(locationSearchCond.getLocation())
                || locationSearchCond.getRadius() == null;
    }

    private boolean isCurrentLocationEmpty(LocationDto currentLocation) {
        return currentLocation == null
                || currentLocation.getLatitude() == null
                || currentLocation.getLongitude() == null;
    }

    private BooleanBuilder allContains(String wordCond) {
        BooleanBuilder builder = new BooleanBuilder();

        return builder.or(nameContains(wordCond))
                .or(addressContains(wordCond))
                .or(phoneNumberContains(wordCond));
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
