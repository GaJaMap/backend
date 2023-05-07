package com.map.gaja.bundle.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BundleCreateRequest {
    @Schema(name = "번들 이름")
    private String name;
}
