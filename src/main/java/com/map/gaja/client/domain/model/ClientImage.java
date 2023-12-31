package com.map.gaja.client.domain.model;

import com.map.gaja.client.domain.exception.InvalidFileException;
import com.map.gaja.global.auditing.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.UUID;

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

    public static ClientImage create(String loginEmail, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String savedPath = createFilePath(loginEmail, originalFilename);
        return new ClientImage(originalFilename, savedPath);
    }

    private static String createFilePath(String loginEmail, String originalFilename) {
        String uuidFileName = createUuidFileName(originalFilename);
        return loginEmail + "/" + uuidFileName;
    }

    private static String createUuidFileName(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String ext = extractExtension(originalFilename);
        return uuid + "." + ext;
    }

    private static String extractExtension(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        if(pos == -1){ // 확장자를 파악할 수 없는 파일
            throw new InvalidFileException();
        }
        return originalFilename.substring(pos + 1);
    }

    private ClientImage(String originalFilename, String savedPath) {
        if (!StringUtils.hasText(originalFilename) || !StringUtils.hasText(savedPath)) {
            throw new InvalidFileException();
        }

        this.originalName = originalFilename;
        this.savedPath = savedPath;
        this.isDeleted = false;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
