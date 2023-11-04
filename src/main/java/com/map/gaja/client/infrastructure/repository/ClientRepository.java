package com.map.gaja.client.infrastructure.repository;

import com.map.gaja.client.domain.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;


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

    /**
     * 삭제된 그룹에 속한 클라이언트 전부 삭제
     */
    @Modifying
    @Query(value = "DELETE FROM client c USING group_set g WHERE g.is_deleted = true", nativeQuery = true)
    @Transactional
    int deleteClientsInDeletedGroup();

    /**
     * Client C(R)UD시에 Group.clientCount 동시성 문제를 해결하기 위해
     * Client와 관련된 Group, ClientImage를 비관적 락을 걸어서 조회
     */
    @Query("SELECT c FROM Client c " +
            "JOIN FETCH c.group g " +
            "WHERE c.id = :clientId AND g.isDeleted = false")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Client> findClientWithGroupForUpdate(long clientId);
}
