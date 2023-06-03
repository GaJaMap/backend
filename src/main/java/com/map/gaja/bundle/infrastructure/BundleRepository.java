package com.map.gaja.bundle.infrastructure;

import com.map.gaja.bundle.domain.model.Bundle;
import com.map.gaja.bundle.presentation.dto.response.BundleInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BundleRepository extends JpaRepository<Bundle, Long> {
    @Query("SELECT b.id as bundleId, b.name as bundleName, b.clientCount as clientCount FROM Bundle b WHERE b.user.id = :userId")
    Slice<BundleInfo> findBundleByUserId(@Param(value = "userId") Long userId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Bundle b WHERE b.id = :bundleId AND b.user.id = :userId")
    int deleteByIdAndUserId(@Param(value = "bundleId") Long bundleId, @Param(value = "userId") Long userId);
}
