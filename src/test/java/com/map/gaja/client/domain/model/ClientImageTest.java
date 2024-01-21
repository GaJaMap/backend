package com.map.gaja.client.domain.model;

import com.map.gaja.TestEntityCreator;
import com.map.gaja.client.domain.exception.InvalidFileException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class ClientImageTest {
    @Test
    void aaa() {
        String excludedExtensionFileName = "test";

        Assertions.assertThrows(InvalidFileException.class, () -> {
            ClientImage.create("aaaa", TestEntityCreator.createMockFile(excludedExtensionFileName));
        });
    }

}