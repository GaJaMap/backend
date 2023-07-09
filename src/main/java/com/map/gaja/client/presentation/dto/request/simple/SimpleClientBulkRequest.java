package com.map.gaja.client.presentation.dto.request.simple;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleClientBulkRequest {
    @Valid
    @Size(min = 1, max = 300)
    private List<SimpleNewClientRequest> clients;
}
