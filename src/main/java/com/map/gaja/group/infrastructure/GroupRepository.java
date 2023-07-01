package com.map.gaja.group.infrastructure;

import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.presentation.dto.response.GroupInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    @Query("SELECT g.id as groupId, g.name as groupName, g.clientCount as clientCount FROM Group g WHERE g.user.id = :userId")
    Slice<GroupInfo> findGroupByUserId(@Param(value = "userId") Long userId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Group g WHERE g.id = :groupId AND g.user.id = :userId")
    int deleteByIdAndUserId(@Param(value = "groupId") Long groupId, @Param(value = "userId") Long userId);

    @Query("SELECT g FROM Group g WHERE g.id = :groupId AND g.user.id = :userId")
    Optional<Group> findByIdAndUserId(@Param(value = "groupId") Long groupId, @Param(value = "userId") Long userId);

    @Query("SELECT g FROM Group g INNER JOIN g.user WHERE g.id = :groupId AND g.user.email = :email") // 임시로 만듦
    Optional<Group> findByIdAndUserEmail(@Param(value = "groupId") Long groupId, @Param(value = "email") String email);
}
