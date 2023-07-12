package com.map.gaja.client.infrastructure.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
            log.info("저장 완료. S3 저장위치 = {}", s3FileUrl);

            return createStoredFileDto(s3FileUrl, file.getOriginalFilename());
        } catch (RuntimeException e) {
            log.error("S3 문제로 파일 저장 실패", e);
            throw new S3NotWorkingException(e);
        } catch (IOException e) {
            log.warn("파일 문제로 저장 실패 file={}", file);
            throw new InvalidFileException(e);
        }
    }

    public boolean removeFile(String s3ObjectUri) {
        try {
            if (!isFileStoredInS3(s3ObjectUri)) {
                log.info("제거할 파일을 찾을 수 없습니다. s3ObjectUri={}", s3ObjectUri);
                return false;
            }

            amazonS3Client.deleteObject(bucket, s3ObjectUri);
            log.info("파일 제거 성공. 제거한 파일={}", s3ObjectUri);
        } catch (RuntimeException e) {
            log.error("S3 문제로 파일 제거 실패", e);
            throw new S3NotWorkingException(e);
        }

        return true;
    }

    public void deleteAllFile(String email) {
        String folderPath = email + "/"; //s3에 파일들이 "email@gmail.com/" 경로의 폴더 안에 저장됨

        List<S3ObjectSummary> allFiles = new ArrayList<>(); //파일 객체를 가져올 리스트 생성

        //s3에서 한번 요청으로 가져오는 데이터는 최대 1000개라서 그 이상으로 가져오려면 반복적으로 호출해야됨.
        String continueToken = null;
        do {
            ListObjectsV2Request request = new ListObjectsV2Request()
                    .withBucketName(bucket)
                    .withPrefix(folderPath)
                    .withContinuationToken(continueToken);
            ListObjectsV2Result result = amazonS3Client.listObjectsV2(request);

            // 가져온 객체를 리스트에 추가
            allFiles.addAll(result.getObjectSummaries());

            //1000개가 넘었을 때 가져올 데이터가 더 있는지 확인 -> s3에서 주는 자체적인 고유 식별자 값이라 어떤 것인지 모름 null 또는 문자열임
            continueToken = result.getNextContinuationToken();
        } while (continueToken != null);

        //모든 파일 삭제
        for (S3ObjectSummary summary : allFiles) {
            amazonS3Client.deleteObject(bucket, summary.getKey());
        }
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