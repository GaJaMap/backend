package com.map.gaja.client.domain.exception;

public class ClientNotFoundException extends RuntimeException {
    private Long clinetId;
    public ClientNotFoundException(Long clinetId) {
        this.clinetId = clinetId;
    }
}
