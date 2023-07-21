package com.map.gaja.client.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * WEB에서 엑셀 파일 파싱 후 validation에 걸렸을 시에 발생하는 예외
 */
@Getter
public class InvalidClientRowDataException extends RuntimeException {
    private String message = "실패/성공 : %d / %d <br>" +
            "제약조건을 준수해서 엑셀 데이터를 작성해주세요. <br><br>" +
            "다음 줄의 데이터가 잘못되었습니다. <br>";
    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    public InvalidClientRowDataException(int totalSize, List<Integer> failRowIdx) {
        StringBuilder resultMessageBuilder = new StringBuilder();
        resultMessageBuilder.append(String.format(message, failRowIdx.size(), totalSize));
        resultMessageBuilder.append("{ ");
        resultMessageBuilder.append(getFailIdxListString(failRowIdx));
        resultMessageBuilder.append(" }");

        message = resultMessageBuilder.toString();
    }

    private String getFailIdxListString(List<Integer> failRowIdx) {
        StringBuilder failListString = new StringBuilder();
        failRowIdx.forEach(idx -> failListString.append(idx + ", "));
        return failListString.toString().substring(0, failListString.length() - 2);
    }
}
