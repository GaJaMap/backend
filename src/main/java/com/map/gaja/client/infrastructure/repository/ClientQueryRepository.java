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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

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

    public Slice<ClientResponse> findClientByConditions(Long bundleId, NearbyClientSearchRequest locationSearchCond, String wordCond, Pageable pageable) {
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
                .where(nameContains(wordCond), isClientInRadius(locationSearchCond), bundleIdEq(bundleId))
                .orderBy(distanceAsc(locationSearchCond), client.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1)
                .fetch();

        boolean hasNext = result.size() > pageable.getPageSize();
        if (hasNext) {
            result.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(result, pageable, hasNext);
    }

    public Optional<Client> findClient(long bundleId, long clientId) {
        Client result = query
                .selectFrom(client)
                .where(client.id.eq(clientId).and(client.bundle.id.eq(bundleId)))
                .fetchFirst();

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

    private BooleanExpression bundleIdEq(Long bundleId) {
        return bundleId != null ? client.bundle.id.eq(bundleId) : null;
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
