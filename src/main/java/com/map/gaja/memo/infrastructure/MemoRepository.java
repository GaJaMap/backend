package com.map.gaja.memo.infrastructure;

import com.map.gaja.memo.domain.model.Memo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemoRepository extends JpaRepository<Memo, Long> {
    @Query("SELECT m FROM Memo m " +
            "WHERE m.id=:memoId AND m.client.id=:clientId")
    Optional<Memo> findByIdAndClient(@Param("memoId") Long memoId, @Param("clientId") Long clientId);

    @Query("SELECT m FROM Memo m " +
            "WHERE m.client.id = :clientId AND m.user.id = :userId")
    Slice<Memo> findPageByClientId(@Param("clientId") Long clientId, @Param("userId") Long userId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Memo m " +
            "WHERE m.id=:memoId AND m.user.id=:userId")
    void deleteByIdAndUser(@Param("memoId") Long memoId, @Param("userId") Long userId);
}
