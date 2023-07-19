package com.map.gaja.client.infrastructure.repository;

import com.map.gaja.client.domain.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ClientRepository extends JpaRepository<Client, Long> {
    @Modifying
    @Query("DELETE FROM Client c WHERE c.group.id = :groupId")
    void deleteByGroupId(@Param(value = "groupId") Long groupId);


    /**
     * 그룹 내에 특정 고객들 제거를 위한 기능
     * clientIds와 관련된 ClientImage의 isDeleted 필드를 true로 표시
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE ClientImage ci SET ci.isDeleted = true " +
            "WHERE ci.id IN (SELECT c.clientImage.id FROM Client c WHERE c.id IN :clientIds)")
    void markClientImageAsDeleted(List<Long> clientIds);

    /**
     * 그룹 내에 특정 고객들 제거를 위한 기능
     * clientIds에 있는 Client 전부 제거
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Client c " +
            "WHERE c.id IN :clientIds")
    void deleteClientsInClientIds(List<Long> clientIds);
}
