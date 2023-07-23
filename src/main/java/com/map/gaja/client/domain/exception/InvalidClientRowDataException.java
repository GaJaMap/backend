package com.map.gaja.client.domain.exception;

import com.map.gaja.client.presentation.dto.response.InvalidExcelDataResponse;
import com.map.gaja.global.exception.WebException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * WEB에서 엑셀 파일 파싱 후 validation에 걸렸을 시에 발생하는 예외
 */
@Getter
public class InvalidClientRowDataException extends WebException {
    public InvalidClientRowDataException(InvalidExcelDataResponse invalidResponse) {
        super(HttpStatus.BAD_REQUEST, invalidResponse);
    }
}
