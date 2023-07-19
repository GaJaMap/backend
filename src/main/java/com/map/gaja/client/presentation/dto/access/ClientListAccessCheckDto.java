package com.map.gaja.client.presentation.dto.access;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientListAccessCheckDto {
    private String userEmail;
    private long groupId;
    private List<Long> clientIds;
}
