package com.map.gaja.bundle.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class BundleResponse {
    @Schema(description = "다음 페이지가 있는 지")
    private boolean hasNext;

    private List<BundleInfo> bundleInfos;
}
