package com.map.gaja.group.infrastructure;

import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.presentation.dto.response.GroupInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    /**
     * group 페이징 조회
     */
    @Query("SELECT g.id as groupId, g.name as groupName, g.clientCount as clientCount FROM Group g WHERE g.user.id = :userId AND g.isDeleted = false")
    Slice<GroupInfo> findGroupByUserId(@Param(value = "userId") Long userId, Pageable pageable);

    /**
     * groupId와 userId 값을 가진 삭제할 그룹을  isDeleted 필드 true로 변경
     */
    @Modifying
    @Query("UPDATE Group g SET g.isDeleted = true WHERE g.id = :groupId AND g.user.id = :userId")
    int deleteByIdAndUserId(@Param(value = "groupId") Long groupId, @Param(value = "userId") Long userId);

    /**
     * groupId와 userId 값을 가진 삭제되지 않은 그룹 조회
     */
    @Query("SELECT g FROM Group g WHERE g.id = :groupId AND g.user.id = :userId AND g.isDeleted = false")
    Optional<Group> findByIdAndUserId(@Param(value = "groupId") Long groupId, @Param(value = "userId") Long userId);

    @Query("SELECT g FROM Group g INNER JOIN g.user WHERE g.id = :groupId AND g.user.email = :email AND g.isDeleted = false")
        // 임시로 만듦
    Optional<Group> findByIdAndUserEmail(@Param(value = "groupId") Long groupId, @Param(value = "email") String email);

    /**
     * 회원탈퇴한 유저의 그룹 isDeleted true로 변경
     */
    @Modifying
    @Query("UPDATE Group g SET g.isDeleted = true WHERE g.user.id IN (SELECT u.id FROM User u WHERE u.active = false)")
    @Transactional
    int deleteByWithdrawalUser();

    /**
     * isDeleted true인 group들 전부 삭제
     */
    @Modifying
    @Query(value = "DELETE FROM Group g WHERE g.isDeleted = true")
    @Transactional
    int deleteMarkedGroups();

    /**
     * 사용자가 최근에 참조한 그룹 정보 조회
     */
    @Query("SELECT g.id as groupId, g.name as groupName, g.clientCount as clientCount FROM Group g WHERE g.id = :groupId AND g.isDeleted = false")
    Optional<GroupInfo> findGroupInfoById(@Param(value = "groupId") Long groupId);

    /**
     * Client C(R)UD시에 Group.clientCount 동시성 문제를 해결하기 위해
     * 비관적 락은 건 Group 조회
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT g FROM Group g WHERE g.id = :groupId AND g.isDeleted = false")
    Optional<Group> findGroupByIdForUpdate(@Param(value = "groupId") Long groupId);
}
