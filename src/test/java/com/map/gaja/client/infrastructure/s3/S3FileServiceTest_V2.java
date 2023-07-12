package com.map.gaja.client.infrastructure.s3;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class S3FileServiceTest_V2 {
    static {
        System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
    }

    @Autowired
    S3FileService service;

    @Test
    void deleteAllFile() {
        service.deleteAllFile("test@gmail.com");
    }
}