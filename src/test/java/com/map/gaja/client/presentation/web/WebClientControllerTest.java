package com.map.gaja.client.presentation.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.map.gaja.client.application.ClientService;
import com.map.gaja.client.infrastructure.file.FileParsingService;
import com.map.gaja.client.infrastructure.file.FileValidator;
import com.map.gaja.client.infrastructure.file.parser.dto.ParsedClientDto;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import com.map.gaja.client.presentation.dto.response.InvalidExcelDataResponse;
import com.map.gaja.global.authentication.AuthenticationRepository;
import com.map.gaja.global.authentication.PrincipalDetails;
import com.map.gaja.group.application.GroupAccessVerifyService;
import com.map.gaja.group.application.GroupService;
import com.map.gaja.client.infrastructure.geocode.Geocoder;
import com.map.gaja.user.domain.model.Authority;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(WebClientController.class)
@MockBean(JpaMetamodelMappingContext.class)
class WebClientControllerTest {
    private final String excelFilePath = "src/test/resources/static/file/sample-success.xlsx";
    private final String testUrl = "/api/clients/file";

    @Autowired
    MockMvc mvc;

    @MockBean
    FileParsingService parsingService;

    @MockBean
    GroupAccessVerifyService groupAccessVerifyService;

    @MockBean
    ClientService clientService;

    @MockBean
    GroupService groupService;

    @MockBean
    Geocoder geocoder;

    @MockBean
    FileValidator fileValidator;

    @MockBean
    AuthenticationRepository userGetter;

    @Autowired
    ObjectMapper om;

    Long groupId = 1L;

    @BeforeEach
    void beforeEach() {
    }

    @Test
    @DisplayName("엑셀 파일로 저장 성공")
    void parsingSuccessTest() throws Exception {
        MockMultipartFile mockFile = getMockFile();
        List<ParsedClientDto> successList = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            successList.add(createValidClientExcelData(i));
        }
        int successSize = successList.size();
        when(parsingService.parseClientFile(any())).thenReturn(successList);
        when(geocoder.convertToCoordinatesAsync(successList)).thenReturn(Mono.empty());
        when(userGetter.getAuthority()).thenReturn(List.of(Authority.FREE));
        /*
            doAnswer(invocation -> {
                List<ClientExcelData> data = invocation.getArgument(0);
                data.forEach(client -> {
                    client.setLocation(new LocationDto(34d, 128d));
                });
                return null;
            }).when(locationResolver).convertCoordinate(any());
         */

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart(testUrl)
                .file("excelFile", mockFile.getBytes())
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .with(SecurityMockMvcRequestPostProcessors.user(new PrincipalDetails(1L, "test@gmail.com", "FREE")))
                .param("groupId", String.valueOf(groupId));

        mvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk());
        verify(clientService, times(1)).saveClientExcelData(groupId, successList, List.of(Authority.FREE));
    }

    @Test
    @DisplayName("엑셀 파싱 실패")
    void parsingFailTest() throws Exception {
        MockMultipartFile mockFile = getMockFile();
        List<ParsedClientDto> failList = new ArrayList<>();
        int failIdx1 = 2;
        int failIdx2 = 3;
        failList.add(createValidClientExcelData(1));
        failList.add(createInvalidClientExcelData(failIdx1));
        failList.add(createInvalidClientExcelData(failIdx2));
        when(parsingService.parseClientFile(any())).thenReturn(failList);
        when(userGetter.getAuthority()).thenReturn(List.of(Authority.FREE));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart(testUrl)
                .file("excelFile", mockFile.getBytes())
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .with(SecurityMockMvcRequestPostProcessors.user(new PrincipalDetails(1L, "test@gmail.com", "FREE")))
                .param("groupId", String.valueOf(groupId));

        mvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> {
                    byte[] response = result.getResponse().getContentAsByteArray();
                    InvalidExcelDataResponse responseInfo = om.readValue(response, InvalidExcelDataResponse.class);

                    assertThat(responseInfo.getTotalSize()).isEqualTo(failList.size());
                    assertThat(responseInfo.getFailRowIdx()).containsExactly(failIdx1, failIdx2);
                });
    }

    private static ParsedClientDto createValidClientExcelData(int i) {
        return new ParsedClientDto(i, "테스트" + i, "010-1111-1111", "테스트 주소" + i, "테스트 상세 주소" + i, new LocationDto(33d + 0.003 * i, 126d + 0.003 * i), true);
    }

    private static ParsedClientDto createInvalidClientExcelData(int i) {
        return new ParsedClientDto(i, "테스트" + i, "010-1111-1111", "테스트 주소" + i, "테스트 상세 주소" + i, new LocationDto(33d + 0.003 * i, 126d + 0.003 * i), false);
    }

    private MockMultipartFile getMockFile() throws IOException {
        Path path = Paths.get(excelFilePath);
        String originalFileName = "sample-success.xlsx";
        String contentType = "application/vnd.ms-excel";
        byte[] content = Files.readAllBytes(path);
        return new MockMultipartFile("file", originalFileName, contentType, content);
    }
}