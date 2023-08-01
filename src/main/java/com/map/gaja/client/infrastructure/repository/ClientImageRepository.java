package com.map.gaja.client.infrastructure.repository;

import com.map.gaja.client.domain.model.ClientImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientImageRepository extends JpaRepository<ClientImage, Long> {
    /**
     * 파라미터로 받은 클라이언트 이미지 아이디로 클라이언트 이미지 삭제
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM ClientImage ci " +
            "WHERE ci.id IN :ids")
    void deleteClientImagesInIds(@Param(value = "ids") List<Long> ids);

    /**
     * 삭제된 그룹에 속한 클라이언트 이미지 isDeleted true로 변경
     */
    @Modifying
    @Query("UPDATE ClientImage ci SET ci.isDeleted = true WHERE ci.id IN " +
            "(SELECT c.clientImage.id FROM Client c INNER JOIN Group g ON c.group.id = g.id WHERE g.isDeleted = true)")
    int markDeleted();
}
