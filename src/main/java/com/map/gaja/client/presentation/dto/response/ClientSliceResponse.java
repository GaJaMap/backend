package com.map.gaja.client.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ClientSliceResponse {
    private List<ClientResponse> content;
}
