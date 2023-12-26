package com.map.gaja.client.domain.model;

import com.map.gaja.client.domain.exception.InvalidFileException;
import com.map.gaja.global.auditing.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @Column(nullable = false)
    private Boolean isDeleted;

    public ClientImage(String originalName, String savedPath) {
        if (originalName == null || savedPath == null) {
            throw new InvalidFileException();
        }

        this.originalName = originalName;
        this.savedPath = savedPath;
        this.isDeleted = false;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
