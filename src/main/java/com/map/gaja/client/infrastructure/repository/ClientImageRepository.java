package com.map.gaja.client.infrastructure.repository;

import com.map.gaja.client.domain.model.ClientImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientImageRepository extends JpaRepository<ClientImage, Long> {
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM ClientImage ci " +
            "WHERE ci.id IN :ids")
    void deleteClientImagesInIds(@Param(value = "ids") List<Long> ids);
}
