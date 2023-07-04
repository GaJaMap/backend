package com.map.gaja.client.infrastructure.repository;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.infrastructure.repository.querydsl.sql.NativeSqlCreator;
import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import com.map.gaja.client.presentation.dto.subdto.GroupInfoDto;
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

import static com.map.gaja.client.domain.model.QClient.client;
import static com.map.gaja.group.domain.model.QGroup.group;
import static com.map.gaja.user.domain.model.QUser.*;

@Repository
@RequiredArgsConstructor
public class ClientQueryRepository {
    private final JPAQueryFactory query;
    private final NativeSqlCreator mysqlNativeSQLCreator;

    /**
     * 반경 검색 동적 쿼리
     * groupIdList.size < 1 이면 그룹에 포함된 Client가 아니기 때문에 비어있는 ArrayList 반환
     * groupIdList.size >= 1 이면 groupIdList에 포함된 Client를 검색
     * NearbyClientSearchRequest에 위도, 경도 정보가 없다면 생성일로 정렬
     * 위도, 경도 정보가 있다면 위도 경도를 기준으로 거리를 계산하여 거리순으로 정렬
     * NearbyClientSearchRequest에 radius로 검색 반경 설정
     * wordCond가 있다면 Client Name으로 맞는 Client가 있는지 확인
     *
     * @param groupIdList
     * @param locationSearchCond
     * @param wordCond
     * @return
     */
    public List<ClientResponse> findClientByConditions(List<Long> groupIdList, NearbyClientSearchRequest locationSearchCond, String wordCond) {
        if (groupIdList.size() < 1) {
            return new ArrayList<>();
        }

        List<ClientResponse> result = query.select(
                        Projections.constructor(ClientResponse.class,
                                client.id,
//                                client.group.id,
                                Projections.constructor(GroupInfoDto.class, client.group.id, client.group.name),
                                client.name,
                                client.phoneNumber,
                                client.address,
                                client.location,
                                getLocationDistance(locationSearchCond) // 좌표 정보가 없다면 -1을 반환함
                        )
                )
                .from(client)
                .join(client.group, group)
                .where(nameContains(wordCond), isClientInRadius(locationSearchCond), groupIdEq(groupIdList))
                .orderBy(distanceAsc(locationSearchCond), client.createdAt.desc())
                .limit(1000)
                .fetch();

        return result;
    }

    /**
     * 그룹을 패치 조인 해서 Client 검색
     * @param clientId
     * @return
     */
    public Optional<Client> findClientWithGroup(long clientId) {
        Client result = query
                .selectFrom(client)
                .join(client.group, group)
                .where(client.id.eq(clientId))
                .fetchJoin().fetchOne();

        return Optional.ofNullable(result);
    }

    /**
     * Group 내에 있는 Client 조회
     * @param groupId
     * @return
     */
    public List<Client> findByGroup_Id(long groupId) {
        List<Client> result = query
                .selectFrom(client)
                .join(client.group, group)
                .where(group.id.eq(groupId))
                .fetchJoin().fetch();

        return result;
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

    private BooleanExpression groupIdEq(List<Long> groupIdList) {
        if (groupIdList.size() == 1) {
            return client.group.id.eq(groupIdList.get(0));
        }

        return client.group.id.in(groupIdList);
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

    /**
     * 해당 그룹이 클라이언트를 가지고 있는지 확인
     * @param groupId
     * @param clientId
     * @return 가지고 있다면 true
     */
    public boolean hasClientByGroup(Long groupId, Long clientId) {
        Integer result = query.selectOne()
                .from(client)
                .join(client.group, group).on(group.id.eq(groupId))
                .where(
                        client.id.eq(clientId)
                )
                .fetchOne();

        return result != null;
    }

    /**
     * 해당 그룹이 클라이언트를 가지고 있지 않은지 확인
     * @param groupId
     * @param clientId
     * @return 가지고 있지 않다면 true
     */
    public boolean hasNoClientByGroup(Long groupId, Long clientId) {
        return !hasClientByGroup(groupId, clientId);
    }
}
