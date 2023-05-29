package com.map.gaja.bundle.infrastructure;

import com.map.gaja.bundle.domain.model.Bundle;
import com.map.gaja.bundle.presentation.dto.response.BundleInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BundleRepository extends JpaRepository<Bundle, Long> {
    @Query("SELECT b.id, b.name, b.clientCount FROM Bundle b WHERE b.user.id = :userId")
    Page<BundleInfo> findBundleByUserId(@Param(value = "userId") Long userId, Pageable pageable);
}
