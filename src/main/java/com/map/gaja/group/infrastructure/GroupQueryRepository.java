package com.map.gaja.group.infrastructure;

import com.map.gaja.group.domain.model.Group;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.map.gaja.group.domain.model.QGroup.group;
import static com.map.gaja.user.domain.model.QUser.*;

@Repository
@RequiredArgsConstructor
public class GroupQueryRepository {
    private final JPAQueryFactory query;

    /**
     * 해당 Email의 User가 그룹을 가지고 있는지 확인
     * @param groupId 그룹 ID
     * @param userEmail User Email
     * @return 가지고 있다면 true
     */
    public boolean hasGroupByUser(Long groupId, String userEmail) {
        Integer result = query.selectOne()
                .from(group)
                .join(group.user, user).on(user.email.eq(userEmail))
                .where(group.id.eq(groupId))
                .fetchOne();

        return result != null;
    }

    /**
     * 해당 Email의 User가 그룹을 가지고 있지 않은지 확인
     * @param groupId 그룹 ID
     * @param userEmail User Email
     * @return 안가지고 있다면 true
     */
    public boolean hasNoGroupByUser(Long groupId, String userEmail) {
        return !hasGroupByUser(groupId, userEmail);
    }


    /**
     * 해당 Email의 User가 가지고 있는 "삭제되지 않은" 그룹 ID 반환
     * @param userEmail 로그인한 Email
     * @return 가지고 있는 그룹의 ID List
     */
    public List<Long> findActiveGroup(String userEmail) {
        return query.select(group.id)
                .from(group)
                .join(group.user, user).on(user.email.eq(userEmail))
                .where(group.isDeleted.eq(Boolean.FALSE))
                .fetch();
    }

    /**
     * 해당 Group과 연관된 User를 함께 가져옴
     * @param groupId 그룹 ID
     * @return 찾은 Group 반환
     */
    public Optional<Group> findGroupWithUser(Long groupId) {
        Group result = query.selectFrom(group)
                .join(group.user, user)
                .where(group.id.eq(groupId))
                .fetchJoin().fetchOne();

        return Optional.ofNullable(result);
    }
}
