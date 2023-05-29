package com.map.gaja.bundle.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class BundleResponse {
    @Schema(description = "총 번들 수")
    private Long total;

    private List<BundleInfo> bundleInfos;
}
