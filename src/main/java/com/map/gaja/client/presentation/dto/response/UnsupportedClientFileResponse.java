package com.map.gaja.client.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnsupportedClientFileResponse {
    private String fileType;

    private String message;

}
