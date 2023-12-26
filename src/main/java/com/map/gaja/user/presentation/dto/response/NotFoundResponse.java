package com.map.gaja.user.presentation.dto.response;

import lombok.Getter;

@Getter
public class NotFoundResponse {
    private String message;

    public NotFoundResponse(String message) {
        this.message = message;
    }
}
