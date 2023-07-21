package com.map.gaja.client.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * WEB에서 엑셀 파일 파싱 후 validation에 걸렸을 시에 발생하는 예외
 */
@Getter
public class InvalidClientRowDataException extends RuntimeException {
    private String message = "명시되어있는 제약조건을 준수해서 엑셀 데이터를 작성해주세요. <br>" +
            "%d의 데이터 중 %d의 데이터가 성공했습니다. <br><br>" +
            "다음 줄의 데이터가 잘못되었습니다. <br>";
    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    public InvalidClientRowDataException(int totalSize, List<Integer> failRowIdx) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(message, totalSize, failRowIdx.size()));
        sb.append("{");
        failRowIdx.forEach(idx -> sb.append(idx + ", "));
        sb.append("}");

        message = sb.toString();
    }
}
