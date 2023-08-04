package com.map.gaja.client.presentation.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.map.gaja.client.apllication.ClientAccessVerifyService;
import com.map.gaja.client.apllication.ClientQueryService;
import com.map.gaja.client.apllication.ClientService;
import com.map.gaja.client.infrastructure.s3.S3FileService;
import com.map.gaja.client.presentation.dto.request.ClientIdsRequest;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.request.simple.SimpleClientBulkRequest;
import com.map.gaja.client.presentation.dto.request.simple.SimpleNewClientRequest;
import com.map.gaja.client.presentation.dto.request.subdto.AddressDto;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import com.map.gaja.global.authentication.PrincipalDetails;
import com.map.gaja.global.exception.ValidationErrorInfo;
import com.map.gaja.group.application.GroupAccessVerifyService;
import com.map.gaja.group.domain.exception.GroupNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(ClientController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class ClientPostControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    ClientService clientService;
    @MockBean
    ClientQueryService clientQueryService;
    @MockBean
    ClientAccessVerifyService clientAccessVerifyService;
    @MockBean
    GroupAccessVerifyService groupAccessVerifyService;
    @MockBean
    S3FileService fileService;

    private ObjectMapper om;

    static Long groupId = 1L;


    @BeforeEach
    void beforeEach() {
        om = new ObjectMapper();
    }

    static class ClientRequestCreator {
        static String imageFilePath = "src/test/resources/static/file/test-image.png";

        static NewClientRequest createValidNewRequest() {
            return new NewClientRequest("테스트", groupId,
                    "010-1111-2222",
                    new AddressDto("서울특별시 중구 세종대로 110", "1동 100호"),
                    new LocationDto(34d,127d), null, true);
        }

        /**
         * 이미지랑 같이 요청
         */
        private static MockHttpServletRequestBuilder createRequestWithImage(String testUrl) throws IOException {
            return MockMvcRequestBuilders.multipart(testUrl)
                    .file("clientImage", getImage())
                    .with(csrf())
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                    .with(SecurityMockMvcRequestPostProcessors.user(new PrincipalDetails("test@gmail.com", "FREE")));
        }

        /**
         * 이미지 없이 요청
         */
        private static MockHttpServletRequestBuilder createRequestWithoutImage(String testUrl) {
            return MockMvcRequestBuilders.post(testUrl)
                    .with(csrf())
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                    .with(SecurityMockMvcRequestPostProcessors.user(new PrincipalDetails("test@gmail.com", "FREE")));
        }

        private static byte[] getImage() throws IOException {
            Path path = Paths.get(imageFilePath);
            return Files.readAllBytes(path);
        }
    }

    @Test
    @DisplayName("이미지 없이 고객 등록")
    void addClientWithoutImageTest() throws Exception {
        String testUrl = "/api/clients";
        NewClientRequest request = ClientRequestCreator.createValidNewRequest();
        MockHttpServletRequestBuilder mockRequest = ClientRequestCreator.createRequestWithoutImage(testUrl);
        setNormalField(mockRequest, request);
        mockRequest.param("isBasicImage", String.valueOf(true));

        mvc.perform(mockRequest).andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("이미지와 함께 고객 등록")
    void addClientWithImageTest() throws Exception {
        String testUrl = "/api/clients";
        NewClientRequest request = ClientRequestCreator.createValidNewRequest();
        MockHttpServletRequestBuilder mockRequest = ClientRequestCreator.createRequestWithImage(testUrl);
        setNormalField(mockRequest, request);
        mockRequest.param("isBasicImage", String.valueOf(false));

        mvc.perform(mockRequest).andExpect(MockMvcResultMatchers.status().isCreated());
        verify(clientService, times(1)).saveClientWithImage(any(), any());
    }

    @Test
    @DisplayName("이미지와 함께 고객 저장 중 오류 발생")
    void addClientWithImageExceptionTest() throws Exception {
        String testUrl = "/api/clients";
        NewClientRequest request = ClientRequestCreator.createValidNewRequest();
        MockHttpServletRequestBuilder mockRequest = ClientRequestCreator.createRequestWithImage(testUrl);
        setNormalField(mockRequest, request);
        mockRequest.param("isBasicImage", String.valueOf(false));
        StoredFileDto savedS3TestFile = new StoredFileDto("testFile-uuid", "testFile");
        when(fileService.storeFile(any(), any())).thenReturn(savedS3TestFile);
        when(clientService.saveClientWithImage(any(), any())).thenThrow(new GroupNotFoundException());

        mvc.perform(mockRequest).andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
        verify(clientService, times(1)).saveClientWithImage(any(), any());
        verify(fileService, times(1)).removeFile(savedS3TestFile.getFilePath());
    }

    @Test
    @DisplayName("고객이 Basic-Image를 사용하는데 이미지가 들어옴")
    void addClientWithImageFailTest() throws Exception {
        String testUrl = "/api/clients";
        NewClientRequest request = ClientRequestCreator.createValidNewRequest();
        MockHttpServletRequestBuilder mockRequest = ClientRequestCreator.createRequestWithImage(testUrl);
        setNormalField(mockRequest, request);
        mockRequest.param("isBasicImage", String.valueOf(true));

        MockHttpServletResponse response = mvc.perform(mockRequest).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn().getResponse();

        List<ValidationErrorInfo> validationErrorInfoList = om.readValue(response.getContentAsByteArray(), new TypeReference<>() {});
        Assertions.assertThat(validationErrorInfoList.size()).isEqualTo(1);
        Assertions.assertThat(validationErrorInfoList.get(0).getCode()).isNull();
        System.out.println(validationErrorInfoList.get(0));
    }

    @Test
    @DisplayName("고객이 Basic-Image를 사용하지 않는데 이미지가 없음")
    void addClientWithoutImageFailTest() throws Exception {
        String testUrl = "/api/clients";
        NewClientRequest request = ClientRequestCreator.createValidNewRequest();
        MockHttpServletRequestBuilder mockRequest = ClientRequestCreator.createRequestWithoutImage(testUrl);
        setNormalField(mockRequest, request);
        mockRequest.param("isBasicImage", String.valueOf(false));

        MockHttpServletResponse response = mvc.perform(mockRequest).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn().getResponse();

        List<ValidationErrorInfo> validationErrorInfoList = om.readValue(response.getContentAsByteArray(), new TypeReference<>() {});
        Assertions.assertThat(validationErrorInfoList.size()).isEqualTo(1);
        Assertions.assertThat(validationErrorInfoList.get(0).getCode()).isNull();
        System.out.println(validationErrorInfoList.get(0));
    }

    private void setNormalField(MockHttpServletRequestBuilder mockRequest, NewClientRequest request) {
        mockRequest
                .param("clientName", request.getClientName())
                .param("phoneNumber", request.getPhoneNumber())
                .param("groupId", String.valueOf(request.getGroupId()))
                .param("mainAddress", request.getAddress().getMainAddress())
                .param("detail", request.getAddress().getDetail())
                .param("latitude", String.valueOf(request.getLocation().getLatitude()))
                .param("longitude", String.valueOf(request.getLocation().getLongitude()));
    }

    @Test
    @DisplayName("다수 고객 등록")
    void addSimpleBulkClient() throws Exception {
        String testUrl = "/api/clients/bulk";
        List<SimpleNewClientRequest> clientsRequest = List.of(
                new SimpleNewClientRequest("aaa", "010-1111-2222"),
                new SimpleNewClientRequest("aaa", "010-1111-2222")
        );

        SimpleClientBulkRequest request = new SimpleClientBulkRequest(groupId, clientsRequest);
        String jsonBody = om.writeValueAsString(request);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(testUrl, groupId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .with(SecurityMockMvcRequestPostProcessors.user(new PrincipalDetails("test@gmail.com", "FREE")));

        mvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated());
        verify(clientService, times(1)).saveSimpleClientList(request);

    }
}
