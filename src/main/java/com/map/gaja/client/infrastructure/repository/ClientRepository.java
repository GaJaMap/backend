package com.map.gaja.client.infrastructure.repository;

import com.map.gaja.client.domain.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientRepository extends JpaRepository<Client, Long> {
    @Modifying
    @Query("DELETE FROM Client c WHERE c.bundle.id = :bundleId")
    void deleteByBundleId(@Param(value = "bundleId") Long bundleId);
}
