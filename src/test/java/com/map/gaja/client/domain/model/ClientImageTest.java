package com.map.gaja.client.domain.model;

import com.map.gaja.client.domain.exception.InvalidFileException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientImageTest {
    @Test
    void aaa() {
        Assertions.assertThrows(InvalidFileException.class, () -> {
            new ClientImage("aaaa",null);
        });
    }

}