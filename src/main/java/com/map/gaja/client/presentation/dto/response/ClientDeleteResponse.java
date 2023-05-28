package com.map.gaja.client.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDeleteResponse {
    private int status;
    private Long deletedClientId;
    private String message;
}
