package com.map.gaja.client.domain.model;

import com.map.gaja.global.auditing.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClientImage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_image_id")
    private Long id;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private String savedPath;

    @CreationTimestamp
    private LocalDateTime createdDate;

    public ClientImage(String originalName, String savedPath) {
        this.originalName = originalName;
        this.savedPath = savedPath;
    }
}
