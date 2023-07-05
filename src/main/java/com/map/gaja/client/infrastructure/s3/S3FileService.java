package com.map.gaja.client.infrastructure.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.map.gaja.client.domain.exception.InvalidFileException;
import com.map.gaja.client.domain.exception.S3NotWorkingException;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 저장경로: /{파일확장자}/{uuid}.{파일확장자}
 * 파일 확장자 하위로 저장한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class S3FileService {

    private final AmazonS3Client amazonS3Client;
    private final S3UrlGenerator s3UrlGenerator;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public StoredFileDto storeFile(String loginEmail, MultipartFile file) {
        try (InputStream fileInputStream = file.getInputStream()) {
            String s3FileUrl = storeFileToS3(loginEmail, file, fileInputStream);
            log.info("저장 완료. S3 저장위치 = {}",s3FileUrl);

            return createStoredFileDto(s3FileUrl, file.getOriginalFilename());
        } catch(RuntimeException e) {
            log.error("S3 문제로 파일 저장 실패" , e);
            throw new S3NotWorkingException(e);
        } catch (IOException e) {
            log.warn("파일 문제로 저장 실패 file={}", file);
            throw new InvalidFileException(e);
        }
    }

    public boolean removeFile(String s3FileUrl) {
        String filePath = extractFilePath(s3FileUrl);

        try {
            if (!isFileStoredInS3(filePath)) {
                log.info("제거할 파일을 찾을 수 없습니다. filePath={}", filePath);
                return false;
            }

            amazonS3Client.deleteObject(bucket, filePath);
            log.info("파일 제거 성공. 제거한 파일={}", filePath);
        } catch(RuntimeException e) {
            log.error("S3 문제로 파일 제거 실패" , e);
            throw new S3NotWorkingException(e);
        }

        return true;
    }

    private boolean isFileStoredInS3(String filePath) {
        return amazonS3Client.doesObjectExist(bucket, filePath);
    }

    private StoredFileDto createStoredFileDto(String s3FileUrl, String originalFilename) {
        String filePath = extractFilePath(s3FileUrl);
        return new StoredFileDto(filePath, originalFilename);
    }

    private String storeFileToS3(String loginEmail, MultipartFile file, InputStream fileInputStream) {
        String originalFilename = file.getOriginalFilename();
        String storePath = createFilePath(loginEmail, originalFilename);

        ObjectMetadata objectMetadata = getFileMetadata(file);

        amazonS3Client.putObject(
                new PutObjectRequest(bucket, storePath, fileInputStream, objectMetadata)
        );

        String storedPath = amazonS3Client.getUrl(bucket, storePath).toString();
        return decodePath(storedPath);
    }

    private String decodePath(String storedPath) {
        return URLDecoder.decode(storedPath, StandardCharsets.UTF_8);
    }

    private String createFilePath(String loginEmail, String originalFilename) {
        String dirName = loginEmail;
        String storeFileName = createFileName(originalFilename);
        return dirName + "/" + storeFileName;
    }

    private ObjectMetadata getFileMetadata(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        return objectMetadata;
    }

    private String createFileName(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String ext = extractExt(originalFilename);
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    private String extractFilePath(String s3FileUrl) {
        return s3UrlGenerator.extractFilePath(s3FileUrl);
    }
}