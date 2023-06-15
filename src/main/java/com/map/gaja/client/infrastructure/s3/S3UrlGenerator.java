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

    public String getS3Url() {
        return s3Url;
    }
}
