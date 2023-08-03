package com.map.gaja.client.presentation.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.map.gaja.client.apllication.ClientAccessVerifyService;
import com.map.gaja.client.apllication.ClientQueryService;
import com.map.gaja.client.apllication.ClientService;
import com.map.gaja.client.infrastructure.s3.S3FileService;
import com.map.gaja.client.presentation.dto.request.ClientIdsRequest;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.request.subdto.AddressDto;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import com.map.gaja.global.authentication.PrincipalDetails;
import com.map.gaja.global.exception.ValidationErrorInfo;
import com.map.gaja.group.application.GroupAccessVerifyService;
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
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(ClientController.class)
@MockBean(JpaMetamodelMappingContext.class)
class ClientControllerTest {

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

    Long groupId = 1L;
    Long clientId = 1L;
    private ObjectMapper om;

    @BeforeEach
    void beforeEach() {
        om = new ObjectMapper();
    }

    @Test
    @DisplayName("고객 삭제 성공")
    void deleteClientTest() throws Exception {
        String testUrl = "/api/group/{groupId}/clients/{clientId}";

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(testUrl, groupId, clientId)
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(new PrincipalDetails("test@gmail.com", "FREE")));

        mvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isNoContent());
        verify(clientService, times(1)).deleteClient(clientId);
    }

    @Test
    @DisplayName("다수 고객 제거 성공")
    void deleteBulkClientTest() throws Exception {
        String testUrl = "/api/group/{groupId}/clients/bulk-delete";
        List<Long> clientIds = List.of(1L, 2L, 3L);
        ClientIdsRequest clientIdsRequest = new ClientIdsRequest(clientIds);
        String jsonBody = om.writeValueAsString(clientIdsRequest);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(testUrl, groupId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .with(SecurityMockMvcRequestPostProcessors.user(new PrincipalDetails("test@gmail.com", "FREE")));

        mvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isNoContent());
        verify(clientService, times(1)).deleteBulkClient(groupId, clientIds);
    }

    @Test
    @DisplayName("단일 고객으로 bulk delete 시도")
    void deleteBulkClientTestFail() throws Exception {
        String testUrl = "/api/group/{groupId}/clients/bulk-delete";
        List<Long> clientIds = List.of(1L);
        ClientIdsRequest clientIdsRequest = new ClientIdsRequest(clientIds);
        String jsonBody = om.writeValueAsString(clientIdsRequest);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(testUrl, groupId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .with(SecurityMockMvcRequestPostProcessors.user(new PrincipalDetails("test@gmail.com", "FREE")));

        MockHttpServletResponse response = mvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn().getResponse();
        List<ValidationErrorInfo> validationErrorInfoList = om.readValue(response.getContentAsByteArray(), new TypeReference<>() {});
        ValidationErrorInfo validationErrorInfo = validationErrorInfoList.get(0);
        Assertions.assertThat(validationErrorInfo.getCode()).isEqualTo("Size");
    }

    static class ClientRequestCreator {
        static Long groupId = 1L;
        static String imageFilePath = "src/test/resources/static/file/test-image.png";

        static NewClientRequest createValidNewRequest() {
            return new NewClientRequest("테스트", groupId,
                    "010-1111-2222",
                    new AddressDto("서울특별시 중구 세종대로 110", "1동 100호"),
                    new LocationDto(34d,127d), null, true);
        }

        static MockHttpServletRequestBuilder createValidRequestWithoutImage(String testUrl) {
            NewClientRequest request = ClientRequestCreator.createValidNewRequest();
            MockHttpServletRequestBuilder mockRequest = createRequestWithoutImage(testUrl);
            setNormalField(mockRequest, request);
            return mockRequest
                    .param("isBasicImage", String.valueOf(true));
        }

        static MockHttpServletRequestBuilder createValidRequestWithImage(String testUrl) throws IOException {
            NewClientRequest request = ClientRequestCreator.createValidNewRequest();

            MockHttpServletRequestBuilder mockRequest = createRequestWithImage(testUrl);
            setNormalField(mockRequest, request);
            return mockRequest
                    .param("isBasicImage", String.valueOf(false));
        }

        /**
         * POST로 고객을 등록한다.
         * 기본 이미지가 아닌데 - 이미지 파일이 안들어왔다.
         */
        static MockHttpServletRequestBuilder createInvalidBasicImageRequest(String testUrl) throws IOException {
            NewClientRequest request = ClientRequestCreator.createValidNewRequest();

            MockHttpServletRequestBuilder mockRequest = createRequestWithoutImage(testUrl);
            setNormalField(mockRequest, request);
            return mockRequest
                    .param("isBasicImage", String.valueOf(false));
        }

        /**
         * POST로 고객을 등록한다.
         * 기본 이미지인데 - 이미지 파일이 들어왔다.
         */
        static MockHttpServletRequestBuilder createInvalidBasicImageRequestWithImage(String testUrl) throws IOException {
            NewClientRequest request = ClientRequestCreator.createValidNewRequest();
            MockHttpServletRequestBuilder mockRequest = createRequestWithImage(testUrl);
            setNormalField(mockRequest, request);
            return mockRequest
                    .param("isBasicImage", String.valueOf(true));
        }

        private static void setNormalField(MockHttpServletRequestBuilder mockRequest, NewClientRequest request) {
            mockRequest
                    .param("clientName", request.getClientName())
                    .param("phoneNumber", request.getPhoneNumber())
                    .param("groupId", String.valueOf(request.getGroupId()))
                    .param("mainAddress", request.getAddress().getMainAddress())
                    .param("detail", request.getAddress().getDetail())
                    .param("latitude", String.valueOf(request.getLocation().getLatitude()))
                    .param("longitude", String.valueOf(request.getLocation().getLongitude()));
        }

        /**
         * 이미지와 함께 요청
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
        MockHttpServletRequestBuilder requestBuilder = ClientRequestCreator.createValidRequestWithoutImage(testUrl);

        mvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("이미지와 함께 고객 등록")
    void addClientWithImageTest() throws Exception {
        String testUrl = "/api/clients";
        MockHttpServletRequestBuilder requestBuilder = ClientRequestCreator.createValidRequestWithImage(testUrl);

        mvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("고객이 Basic-Image를 사용하는데 이미지가 들어옴")
    void addClientWithImageFailTest() throws Exception {
        String testUrl = "/api/clients";
        MockHttpServletRequestBuilder requestBuilder = ClientRequestCreator.createInvalidBasicImageRequestWithImage(testUrl);
        MockHttpServletResponse response = mvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn().getResponse();

        List<ValidationErrorInfo> validationErrorInfoList = om.readValue(response.getContentAsByteArray(), new TypeReference<>() {});
        Assertions.assertThat(validationErrorInfoList.size()).isEqualTo(1);
        Assertions.assertThat(validationErrorInfoList.get(0).getCode()).isNull();
        System.out.println(validationErrorInfoList.get(0));
    }

    @Test
    @DisplayName("고객이 Basic-Image를 사용하지 않는데 이미지가 없음")
    void addClientWithoutImageFailTest() throws Exception {
        String testUrl = "/api/clients";
        MockHttpServletRequestBuilder requestBuilder = ClientRequestCreator.createInvalidBasicImageRequest(testUrl);
        MockHttpServletResponse response = mvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn().getResponse();

        List<ValidationErrorInfo> validationErrorInfoList = om.readValue(response.getContentAsByteArray(), new TypeReference<>() {});
        Assertions.assertThat(validationErrorInfoList.size()).isEqualTo(1);
        Assertions.assertThat(validationErrorInfoList.get(0).getCode()).isNull();
        System.out.println(validationErrorInfoList.get(0));
    }

    @Test
    @DisplayName("다수 고객 등록")
    void addSimpleBulkClient() {
    }

    @Test
    void updateClient() {
    }
}