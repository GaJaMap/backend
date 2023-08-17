package com.map.gaja.client.presentation.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.map.gaja.client.apllication.ClientAccessVerifyService;
import com.map.gaja.client.apllication.ClientQueryService;
import com.map.gaja.client.apllication.ClientService;
import com.map.gaja.client.infrastructure.file.FileValidator;
import com.map.gaja.client.infrastructure.s3.S3FileService;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.request.simple.SimpleClientBulkRequest;
import com.map.gaja.client.presentation.dto.request.simple.SimpleNewClientRequest;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import com.map.gaja.global.authentication.PrincipalDetails;
import com.map.gaja.global.exception.ValidationErrorResponse;
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
    @MockBean
    FileValidator fileValidator;

    private ObjectMapper om;

    static Long groupId = 1L;
    static Long clientId = 1L;


    @BeforeEach
    void beforeEach() {
        om = new ObjectMapper();
    }

    @Test
    @DisplayName("이미지 없이 고객 등록")
    void addClientWithoutImageTest() throws Exception {
        String testUrl = "/api/clients";
        NewClientRequest request = ClientRequestCreator.createValidNewRequest(groupId);
        MockHttpServletRequestBuilder mockRequest = ClientRequestCreator.createPostRequestWithoutImage(testUrl);
        ClientRequestCreator.setNormalField(mockRequest, request);
        mockRequest.param("isBasicImage", String.valueOf(true));

        mvc.perform(mockRequest).andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("이미지와 함께 고객 등록")
    void addClientWithImageTest() throws Exception {
        String testUrl = "/api/clients";
        NewClientRequest request = ClientRequestCreator.createValidNewRequest(groupId);
        MockHttpServletRequestBuilder mockRequest = ClientRequestCreator.createPostRequestWithImage(testUrl);
        ClientRequestCreator.setNormalField(mockRequest, request);
        mockRequest.param("isBasicImage", String.valueOf(false));
        when(fileValidator.isAllowedImageType(any())).thenReturn(true);

        mvc.perform(mockRequest).andExpect(MockMvcResultMatchers.status().isCreated());
        verify(clientService, times(1)).saveClientWithImage(any(), any());
    }

    @Test
    @DisplayName("이미지와 함께 고객 저장 중 오류 발생")
    void addClientWithImageExceptionTest() throws Exception {
        String testUrl = "/api/clients";
        NewClientRequest request = ClientRequestCreator.createValidNewRequest(groupId);
        MockHttpServletRequestBuilder mockRequest = ClientRequestCreator.createPostRequestWithImage(testUrl);
        ClientRequestCreator.setNormalField(mockRequest, request);
        mockRequest.param("isBasicImage", String.valueOf(false));
        StoredFileDto savedS3TestFile = new StoredFileDto("testFile-uuid", "testFile");
        when(fileService.storeFile(any(), any())).thenReturn(savedS3TestFile);
        when(clientService.saveClientWithImage(any(), any())).thenThrow(new GroupNotFoundException());
        when(fileValidator.isAllowedImageType(any())).thenReturn(true);

        mvc.perform(mockRequest).andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
        verify(clientService, times(1)).saveClientWithImage(any(), any());
        verify(fileService, times(1)).removeFile(savedS3TestFile.getFilePath());
    }

    @Test
    @DisplayName("고객이 Basic-Image를 사용하는데 이미지가 들어옴")
    void addClientWithImageFailTest() throws Exception {
        String testUrl = "/api/clients";
        NewClientRequest request = ClientRequestCreator.createValidNewRequest(groupId);
        MockHttpServletRequestBuilder mockRequest = ClientRequestCreator.createPostRequestWithImage(testUrl);
        ClientRequestCreator.setNormalField(mockRequest, request);
        mockRequest.param("isBasicImage", String.valueOf(true));

        MockHttpServletResponse response = mvc.perform(mockRequest).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn().getResponse();

        List<ValidationErrorResponse> validationErrorResponseList = om.readValue(response.getContentAsByteArray(), new TypeReference<>() {});
        Assertions.assertThat(validationErrorResponseList.size()).isEqualTo(1);
        Assertions.assertThat(validationErrorResponseList.get(0).getCode()).isNull();
        System.out.println(validationErrorResponseList.get(0));
    }

    @Test
    @DisplayName("고객이 Basic-Image를 사용하지 않는데 이미지가 없음")
    void addClientWithoutImageFailTest() throws Exception {
        String testUrl = "/api/clients";
        NewClientRequest request = ClientRequestCreator.createValidNewRequest(groupId);
        MockHttpServletRequestBuilder mockRequest = ClientRequestCreator.createPostRequestWithoutImage(testUrl);
        ClientRequestCreator.setNormalField(mockRequest, request);
        mockRequest.param("isBasicImage", String.valueOf(false));

        MockHttpServletResponse response = mvc.perform(mockRequest).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn().getResponse();

        List<ValidationErrorResponse> validationErrorResponseList = om.readValue(response.getContentAsByteArray(), new TypeReference<>() {});
        Assertions.assertThat(validationErrorResponseList.size()).isEqualTo(1);
        Assertions.assertThat(validationErrorResponseList.get(0).getCode()).isNull();
        System.out.println(validationErrorResponseList.get(0));
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
