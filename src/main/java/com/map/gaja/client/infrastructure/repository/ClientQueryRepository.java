package com.map.gaja.client.infrastructure.repository;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.infrastructure.repository.querydsl.sql.NativeSqlCreator;
import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import com.map.gaja.client.presentation.dto.subdto.GroupInfoDto;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.map.gaja.client.domain.model.QClient.client;
import static com.map.gaja.client.domain.model.QClientImage.*;
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
     * 위도 경도를 기준으로 거리를 계산하여 거리순으로 정렬
     * NearbyClientSearchRequest에 radius로 검색 반경 설정
     * wordCond가 있다면 Client Name으로 맞는 Client가 있는지 확인
     *
     * @param groupIdList
     * @param locationSearchCond
     * @param wordCond
     * @return
     */
    public List<ClientResponse> findClientByConditions(List<Long> groupIdList, NearbyClientSearchRequest locationSearchCond, @Nullable String wordCond) {
        if (groupIdList.size() < 1) {
            return new ArrayList<>();
        }

        List<ClientResponse> result = query.select(
                        Projections.constructor(ClientResponse.class,
                                client.id,
                                Projections.constructor(GroupInfoDto.class, client.group.id, client.group.name),
                                client.name,
                                client.phoneNumber,
                                client.address,
                                client.location,
                                Projections.constructor(StoredFileDto.class, client.clientImage.savedPath, client.clientImage.originalName),
                                getCalcDistanceWithNativeSQL(locationSearchCond.getLocation())
                        )
                )
                .from(client)
                .join(client.group, group)
                .leftJoin(client.clientImage, clientImage)
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
                .leftJoin(client.clientImage, clientImage)
                .where(client.id.eq(clientId))
                .fetchJoin().fetchOne();

        return Optional.ofNullable(result);
    }

    /**
     * Group 내에 있는 Client 조회
     * @param groupId
     * @return
     */
    public List<Client> findByGroup_Id(long groupId, @Nullable String nameCond) {
        List<Client> result = query
                .selectFrom(client)
                .join(client.group, group)
                .leftJoin(client.clientImage, clientImage)
                .where(group.id.eq(groupId), nameContains(nameCond))
                .fetchJoin().fetch();

        return result;
    }

    private OrderSpecifier<?> distanceAsc(NearbyClientSearchRequest locationCond) {
        return getCalcDistanceWithNativeSQL(locationCond.getLocation()).asc();
    }

    private BooleanExpression isClientInRadius(NearbyClientSearchRequest locationSearchCond) {
        LocationDto currentLocation = locationSearchCond.getLocation();
        return getCalcDistanceWithNativeSQL(currentLocation)
                .loe(locationSearchCond.getRadius());
    }

    private NumberExpression<Double> getCalcDistanceWithNativeSQL(LocationDto currentLocation) {
        return mysqlNativeSQLCreator.createCalcDistanceSQL(
                currentLocation.getLongitude(), currentLocation.getLatitude(),
                client.location.longitude, client.location.latitude
        );
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

    /**
     * loginEmail User가 가지고 있는 Client 전체 검색.
     * @param loginEmail 로그인한 이메일
     * @param nameCond 이름 검색 조건
     * @return
     */
    public List<ClientResponse> findAllClientByEmail(String loginEmail, @Nullable String nameCond) {
        List<ClientResponse> result = query
                .select(
                        Projections.constructor(ClientResponse.class,
                                client.id,
                                Projections.constructor(GroupInfoDto.class, client.group.id, client.group.name),
                                client.name,
                                client.phoneNumber,
                                client.address,
                                client.location,
                                Projections.constructor(StoredFileDto.class, client.clientImage.savedPath, client.clientImage.originalName)
                        )
                )
                .from(client)
                .leftJoin(client.clientImage, clientImage)
                .join(client.group, group)
                .join(client.group.user, user)
                .where(user.email.eq(loginEmail), nameContains(nameCond))
                .orderBy(client.createdAt.desc())
                .fetch();

        return result;
    }
}
