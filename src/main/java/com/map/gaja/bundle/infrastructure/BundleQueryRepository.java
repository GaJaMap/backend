package com.map.gaja.bundle.infrastructure;

import com.map.gaja.bundle.domain.model.QBundle;
import com.map.gaja.user.domain.model.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.map.gaja.bundle.domain.model.QBundle.*;
import static com.map.gaja.user.domain.model.QUser.*;

@Repository
@RequiredArgsConstructor
public class BundleQueryRepository {
    private final JPAQueryFactory query;

    /**
     * 해당 Email의 User가 번들을 가지고 있는지 확인
     * @param bundleId 번들 ID
     * @param userEmail User Email
     * @return 가지고 있다면 true
     */
    public boolean hasBundleByUser(Long bundleId, String userEmail) {
        Integer result = query.selectOne()
                .from(bundle)
                .join(bundle.user, user).on(user.email.eq(userEmail))
                .where(bundle.id.eq(bundleId))
                .fetchOne();

        return result != null;
    }

    /**
     * 해당 Email의 User가 번들을 가지고 있지 않은지 확인
     * @param bundleId 번들 ID
     * @param userEmail User Email
     * @return 안가지고 있다면 true
     */
    public boolean hasNoBundleByUser(Long bundleId, String userEmail) {
        return !hasBundleByUser(bundleId, userEmail);
    }
}
