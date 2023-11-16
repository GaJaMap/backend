package com.map.gaja.client.infrastructure.file.parser;

import com.map.gaja.client.infrastructure.file.parser.dto.RowVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 속 데이터로 RowData를 만드는데만 집중
 *
 * 프로토타입 빈이기 떄문에 싱글톤과 사용할 때 주의하기
 *
 * 작업 종료 시 close()를 호출하거나 try-with-resource를 사용할 것
 * supports(file) -> init(file) -> ... -> close()
 */
public interface FileParser extends AutoCloseable {
    /**
     * 구현체가 파일을 지원하는지 확인
     * init을 하기전에 호출해서 확인할 것
     */
    boolean supports(MultipartFile file);

    /**
     * 파일 데이터를 내부적으로 연결하고, 데이터를 파싱할 위치를 파악함
     * nextRowData 메소드 실행 전 반드시 init()을 호출할 것
     * @param file 데이터를 가져올 파일
     */
    void init(MultipartFile file);

    /**
     * @return 다음 Row에 파싱할 데이터가 있는지
     */
    boolean hasMoreData();

    /**
     * 한 줄(행)의 데이터를 읽어서 반환
     * 데이터가 없다면 null 반환
     */
    RowVO nextRowData();

    /**
     * 잘못된 줄을 정확하게 사용자에게 말해주기 위해 제공
     * @return 데이터가 시작하는 줄(행)의 index 반환
     */
    int getStartRowIndex();
}
