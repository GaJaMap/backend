//package com.map.gaja.client.infrastructure.geocode;
//
//import com.map.gaja.client.infrastructure.file.parser.dto.ParsedClientDto;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//class KakaoGeoApiTest {
//    @Autowired
//    private KakaoGeoApi kakaoGeoApi;
//
//
//    @DisplayName("도로명 주소를 위경도로 변환하기 위한 카카오 api 요청 성공")
//    @Test
//    void request() {
//        //given
//        ParsedClientDto parsedClientDto = new ParsedClientDto(0, "", "010-1111-1111", "서울특별시 강남구", "", null, false);
//
//        //when
//        ResponseEntity<String> response = kakaoGeoApi.request(parsedClientDto).block();
//        System.out.println(response.getBody());
//
//        //then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }
//}