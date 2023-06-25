package com.map.gaja.bundle.infrastructure;

import com.map.gaja.bundle.domain.model.Bundle;
import com.map.gaja.bundle.presentation.dto.response.BundleInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BundleRepository extends JpaRepository<Bundle, Long> {
    @Query("SELECT b.id as bundleId, b.name as bundleName, b.clientCount as clientCount FROM Bundle b WHERE b.user.id = :userId")
    Slice<BundleInfo> findBundleByUserId(@Param(value = "userId") Long userId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Bundle b WHERE b.id = :bundleId AND b.user.id = :userId")
    int deleteByIdAndUserId(@Param(value = "bundleId") Long bundleId, @Param(value = "userId") Long userId);

    @Query("SELECT b FROM Bundle b WHERE b.id = :bundleId AND b.user.id = :userId")
    Optional<Bundle> findByIdAndUserId(@Param(value = "bundleId") Long bundleId, @Param(value = "userId") Long userId);

    @Query("SELECT b FROM Bundle b INNER JOIN b.user WHERE b.id = :bundleId AND b.user.email = :email") // 임시로 만듦
    Optional<Bundle> findByIdAndUserEmail(@Param(value = "bundleId") Long bundleId, @Param(value = "email") String email);
}
