package com.map.gaja.client.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvalidExcelDataResponse {
    private int totalSize;
    private List<Integer> failRowIdx;
}
