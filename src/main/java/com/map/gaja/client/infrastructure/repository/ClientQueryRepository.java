package com.map.gaja.client.infrastructure.repository;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.infrastructure.repository.querydsl.sql.NativeSqlCreator;
import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.map.gaja.bundle.domain.model.QBundle.*;
import static com.map.gaja.client.domain.model.QClient.client;
import static com.map.gaja.user.domain.model.QUser.*;

@Repository
@RequiredArgsConstructor
public class ClientQueryRepository {
    private final JPAQueryFactory query;
    private final NativeSqlCreator mysqlNativeSQLCreator;

    public List<ClientResponse> findClientByConditions(List<Long> bundleIdList, NearbyClientSearchRequest locationSearchCond, String wordCond) {
        if (bundleIdList.size() < 1) {
            return new ArrayList<>();
        }

        List<ClientResponse> result = query.select(
                        Projections.constructor(ClientResponse.class,
                                client.id,
                                client.bundle.id,
                                client.name,
                                client.phoneNumber,
                                client.address,
                                client.location,
                                getLocationDistance(locationSearchCond) // 좌표 정보가 없다면 -1을 반환함
                        )
                )
                .from(client)
                .where(nameContains(wordCond), isClientInRadius(locationSearchCond), bundleIdEq(bundleIdList))
                .orderBy(distanceAsc(locationSearchCond), client.createdDate.desc())
                .limit(1000)
                .fetch();

        return result;
    }

    public Optional<Client> findClientWithBundle(long clientId) {
        Client result = query
                .selectFrom(client)
                .join(client.bundle, bundle)
                .where(client.id.eq(clientId))
                .fetchJoin().fetchOne();

        return Optional.ofNullable(result);
    }

    private NumberExpression<Double> getLocationDistance(NearbyClientSearchRequest locationSearchCond) {
        if(isLocationSearchCondEmpty(locationSearchCond)) {
            return Expressions.asNumber(-1.0);
        }

        return getCalcDistanceNativeSQL(locationSearchCond.getLocation());
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
        return mysqlNativeSQLCreator.createCalcDistanceSQL(
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

    private BooleanExpression nameContains(String nameCond) {
        return nameCond != null ? client.name.contains(nameCond) : null;
    }

    private BooleanExpression bundleIdEq(List<Long> bundleIdList) {
        if (bundleIdList.size() == 1) {
            return client.bundle.id.eq(bundleIdList.get(0));
        }

        return client.bundle.id.in(bundleIdList);
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

    private BooleanExpression addressContains(String addressCond) {
        return addressCond != null ? client.address.city.contains(addressCond)
                .or(client.address.province.contains(addressCond))
                .or(client.address.district.contains(addressCond))
                .or(client.address.detail.contains(addressCond)) : null;
    }

    public Optional<Client> findClientByUserAndBundle(String loginEmail, Long bundleId, Long clientId) {
        Client result = query.select(client)
                .from(client)
                .join(client.bundle, bundle).on(bundle.id.eq(bundleId))
                .join(bundle.user, user).on(user.email.eq(loginEmail))
                .where(
                        client.id.eq(clientId)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    /**
     * 해당 번들이 클라이언트를 가지고 있는지 확인
     * @param bundleId
     * @param clientId
     * @return 가지고 있다면 true
     */
    public boolean hasClientByBundle(Long bundleId, Long clientId) {
        Integer result = query.selectOne()
                .from(client)
                .join(client.bundle, bundle).on(bundle.id.eq(bundleId))
                .where(
                        client.id.eq(clientId)
                )
                .fetchOne();

        return result != null;
    }

    /**
     * 해당 번들이 클라이언트를 가지고 있지 않은지 확인
     * @param bundleId
     * @param clientId
     * @return 가지고 있지 않다면 true
     */
    public boolean hasNoClientByBundle(Long bundleId, Long clientId) {
        return !hasClientByBundle(bundleId, clientId);
    }
}
