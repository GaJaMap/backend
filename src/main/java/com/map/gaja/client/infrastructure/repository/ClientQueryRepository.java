package com.map.gaja.client.infrastructure.repository;

import com.map.gaja.client.infrastructure.repository.querydsl.sql.NativeSqlCreator;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.map.gaja.client.domain.model.QClient.client;

@Repository
@RequiredArgsConstructor
public class ClientQueryRepository {
    private final JPAQueryFactory query;
    private final NativeSqlCreator mysqlNativeSQLCreator;

    public Slice<ClientResponse> findClientByConditions(NearbyClientSearchRequest locationSearchCond, String wordCond, Pageable pageable) {
        List<ClientResponse> result = query.select(
                        Projections.constructor(ClientResponse.class,
                                client.id,
                                Expressions.asNumber(1L), // 임시 번들 데이터
                                client.name,
                                client.phoneNumber,
                                getAddressDto(),
                                getLocationDto(),
                                getLocationDistance(locationSearchCond) // 좌표 정보가 없다면 -1을 반환함
                        )
                )
                .from(client)
                .where(allContains(wordCond), isClientInRadius(locationSearchCond))
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

    private NumberExpression<Double> getLocationDistance(NearbyClientSearchRequest locationSearchCond) {
        if(isLocationSearchCondEmpty(locationSearchCond)) {
            return Expressions.asNumber(-1.0);
        }

        return getCalcDistanceNativeSQL(locationSearchCond.getLocation());
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