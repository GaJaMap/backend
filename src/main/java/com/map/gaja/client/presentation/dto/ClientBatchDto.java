package com.map.gaja.client.presentation.dto;

import lombok.Data;
import lombok.Getter;

import javax.validation.Valid;
import java.util.List;

@Data
public class ClientBatchDto {
    @Valid
    private List<ClientDto> clients;
}
