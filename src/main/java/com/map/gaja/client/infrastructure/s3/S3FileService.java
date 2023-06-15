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

    public StoredFileDto storeFile(MultipartFile file) {
        try (InputStream fileInputStream = file.getInputStream()) {
            String s3StoredPath = storeFileToS3(file, fileInputStream);
            log.info("저장 완료. S3 저장위치 = {}",s3StoredPath);

            return createStoredFileDto(file, s3StoredPath);
        } catch(RuntimeException e) {
            log.error("S3 문제로 파일 저장 실패" , e);
            throw new S3NotWorkingException(e);
        } catch (IOException e) {
            log.warn("파일 문제로 저장 실패 file={}", file);
            throw new InvalidFileException(e);
        }
    }

    private StoredFileDto createStoredFileDto(MultipartFile file, String s3FileUrl) {
        String filePath = s3UrlGenerator.extractFilePath(s3FileUrl);
        return new StoredFileDto(filePath, file.getOriginalFilename());
    }

    public boolean removeFile(String storedPath) {
        String filePath = s3UrlGenerator.extractFilePath(storedPath);

        try {
            if (!isStoredInS3(filePath)) {
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

    private boolean isStoredInS3(String storedPath) {
        return amazonS3Client.doesObjectExist(bucket, storedPath);
    }

    private String storeFileToS3(MultipartFile file, InputStream fileInputStream) {
        String originalFilename = file.getOriginalFilename();
        String storePath = getFilePath(originalFilename);

        ObjectMetadata objectMetadata = getFileMetadata(file);

        amazonS3Client.putObject(
                new PutObjectRequest(bucket, storePath, fileInputStream, objectMetadata)
        );

        return amazonS3Client.getUrl(bucket, storePath).toString();
    }

    private String getFilePath(String originalFilename) {
        String dirName = extractExt(originalFilename);
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

}