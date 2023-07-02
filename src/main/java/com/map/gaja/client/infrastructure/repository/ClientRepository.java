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
}
