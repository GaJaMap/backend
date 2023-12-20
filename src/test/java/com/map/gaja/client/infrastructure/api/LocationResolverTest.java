//package com.map.gaja.client.infrastructure.location;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.map.gaja.client.infrastructure.file.parser.dto.ParsedClientDto;
//import okhttp3.mockwebserver.MockResponse;
//import okhttp3.mockwebserver.MockWebServer;
//import org.assertj.core.api.Assertions;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.io.IOException;
//import java.net.URI;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.map.gaja.client.constant.LocationResolverConstant.*;
//import static com.map.gaja.client.constant.LocationResolverConstant.RESPONSE_SIZE_VALUE;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.*;
//
//
//@ExtendWith(MockitoExtension.class)
//class LocationResolverTest {
//    @Mock
//    KakaoGeoApi kakaoGeoApi;
//
//    @InjectMocks
//    LocationResolver locationResolver;
//
//    @Mock
//    ObjectMapper objectMapper;
//
//    @Mock
//    KakaoUriGenerator kakaoUriGenerator;
//
//    MockWebServer mockWebServer;
//    String responseBody = "{\n" +
//            "  \"documents\": [\n" +
//            "    {\n" +
//            "      \"y\": \"35.97664845766847\",\n" +
//            "      \"x\": \"126.99597295767953\"\n" +
//            "    }\n" +
//            "  ]\n" +
//            "}";
//
//    @BeforeEach
//    void setUp() throws IOException {
//        mockWebServer = new MockWebServer();
//        mockWebServer.start();
//    }
//
//    @AfterEach
//    void terminate() throws IOException {
//        mockWebServer.shutdown();
//    }
//
//    @Test
//    @DisplayName("위경도 변환 성공")
//    void convert_coordinate_async_success() {
//        //given
//        MockResponse mockResponse = new MockResponse()
//                .addHeader("Content-Type", "application/json; charset=utf-8")
//                .setBody(responseBody)
//                .setResponseCode(200);
//        mockWebServer.enqueue(mockResponse);
//
//        List<ParsedClientDto> addresses = new ArrayList<>();
//        for (int i = 0; i < 1; i++) {
//            ParsedClientDto parsedClientDto = new ParsedClientDto(i, "", "010-1111-1111", "서울특별시 강남구", "", null, false);
//            addresses.add(parsedClientDto);
//
//            URI uri = createUri()
//            when(kakaoUriGenerator.createUri(addresses.get(i).getAddress()))
//                    .thenReturn(uri);
//            when(kakaoGeoApi.request(parsedClientDto, uri))
//                    .thenThrow(new ClassNotFoundException());
//        }
//
//        //when
//        locationResolver.convertToCoordinatesAsync(addresses).block();
//
//
//        //then
//    }
//
//    private URI createUri() {
//        return UriComponentsBuilder.fromUriString("localhost")
//                .encode(StandardCharsets.UTF_8)
//                .build()
//                .toUri();
//    }
//}