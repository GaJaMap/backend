package com.map.gaja.client.application.geocode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseParserTest {

    @DisplayName("카카오 geo api 응답 값 위경도 값 파싱 성공")
    @Test
    void parse() {
        //given
        ResponseParser responseParser = new ResponseParser(new ObjectMapper());
        String response = "{\"documents\":[{\"address\":{\"address_name\":\"서울 강남구\",\"b_code\":\"1168000000\",\"h_code\":\"1168000000\",\"main_address_no\":\"\",\"mountain_yn\":\"N\",\"region_1depth_name\":\"서울\",\"region_2depth_name\":\"강남구\",\"region_3depth_h_name\":\"\",\"region_3depth_name\":\"\",\"sub_address_no\":\"\",\"x\":\"127.047377408384\",\"y\":\"37.517331925853\"},\"address_name\":\"서울 강남구\",\"address_type\":\"REGION\",\"road_address\":null,\"x\":\"127.047377408384\",\"y\":\"37.517331925853\"}],\"meta\":{\"is_end\":true,\"pageable_count\":1,\"total_count\":1}}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        LocationDto expected = new LocationDto(37.517331925853d, 127.047377408384d);

        //when
        LocationDto actual = responseParser.parse(responseEntity).block();

        //then
        assertThat(actual).isEqualTo(expected);
    }
}