package com.map.gaja.client.infrastructure.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class S3UrlGenerator {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    private String s3Url;

    @PostConstruct
    private void init() {
        String s3UrlFormat = "https://%s.s3.%s.amazonaws.com/";
        s3Url = String.format(s3UrlFormat, bucket, region);
    }

    /**
     * @return S3 URL ex) https://버킷이름.s3.지역.amazonaws.com/
     */
    public String getS3Url() {
        return s3Url;
    }

    /**
     * S3 파일 URL에서 파일 경로만 추출해주는 메소드
     * @param s3FileUrl S3 파일 URL ex) https://버킷이름.s3.지역.amazonaws.com/123/test.jpg
     * @return 파일 경로 ex) 123/test.jpg
     */
    public String extractFilePath(String s3FileUrl) {
        return s3FileUrl.replace(s3Url, "");
    }
}
