package com.map.gaja.client.presentation.dto.access;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientAccessCheckDto {
    private String userEmail;
    private long groupId;
    private long clientId;
}
