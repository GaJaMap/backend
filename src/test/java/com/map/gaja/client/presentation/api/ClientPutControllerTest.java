package com.map.gaja.client.presentation.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.map.gaja.client.apllication.ClientAccessVerifyService;
import com.map.gaja.client.apllication.ClientQueryService;
import com.map.gaja.client.apllication.ClientService;
import com.map.gaja.client.domain.exception.ClientNotFoundException;
import com.map.gaja.client.infrastructure.file.FileValidator;
import com.map.gaja.client.infrastructure.s3.S3FileService;
import com.map.gaja.client.presentation.ClientRequestValidator;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import com.map.gaja.group.application.GroupAccessVerifyService;
import com.map.gaja.group.domain.exception.GroupNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@WebMvcTest(ClientController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class ClientPutControllerTest {
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
    ClientRequestValidator clientRequestValidator;
    final String testUri = "/api/group/{groupId}/clients/{clientId}";


    private ObjectMapper om;

    static Long groupId = 1L;
    static Long clientId = 1L;

    @Test
    @DisplayName("DB상 고객 이미지 유지")
    void updateClientImage() throws Exception {
        NewClientRequest request = ClientRequestCreator.createValidNewRequestWithImage(groupId);
        MockHttpServletRequestBuilder mockRequest = ClientRequestCreator.createPutRequestWithoutImage(testUri, groupId, clientId);
        ClientRequestCreator.setNormalField(mockRequest, request);
        mockRequest.param("isBasicImage", String.valueOf(false));
        mvc.perform(mockRequest).andExpect(MockMvcResultMatchers.status().isOk());
        verify(clientService, times(1)).updateClientWithoutImage(clientId, request);
    }

    @Test
    @DisplayName("고객 이미지 변경")
    void updateClientOtherImage() throws Exception {
        NewClientRequest request = ClientRequestCreator.createValidNewRequest(groupId);
        MockHttpServletRequestBuilder mockRequest = ClientRequestCreator.createPutRequestWithImage(testUri, groupId, clientId);
        ClientRequestCreator.setNormalField(mockRequest, request);
        mockRequest.param("isBasicImage", String.valueOf(false));
        mvc.perform(mockRequest).andExpect(MockMvcResultMatchers.status().isOk());
        verify(clientService, times(1)).updateClientWithNewImage(any(), any(), any());
    }

    @Test
    @DisplayName("고객 이미지 DB 변경 중 예외")
    void updateClientOtherImageException() throws Exception {
        NewClientRequest request = ClientRequestCreator.createValidNewRequest(groupId);

        MockHttpServletRequestBuilder mockRequest = ClientRequestCreator.createPutRequestWithImage(testUri, groupId, clientId);
        ClientRequestCreator.setNormalField(mockRequest, request);
        mockRequest.param("isBasicImage", String.valueOf(false));

        StoredFileDto savedS3TestFile = new StoredFileDto("testFile-uuid", "testFile");
        when(fileService.storeFile(any(), any())).thenReturn(savedS3TestFile);
        doThrow(new ClientNotFoundException()).when(clientService).updateClientWithNewImage(any(),any(), any());

        mvc.perform(mockRequest).andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
        verify(clientService, times(1)).updateClientWithNewImage(any(), any(), any());
        verify(fileService, times(1)).removeFile(savedS3TestFile.getFilePath());
    }

    @Test
    @DisplayName("고객 이미지를 Basic Image로 변경")
    void updateClientBasicImage() throws Exception {
        NewClientRequest request = ClientRequestCreator.createValidNewRequest(groupId);
        MockHttpServletRequestBuilder mockRequest = ClientRequestCreator.createPutRequestWithoutImage(testUri, groupId, clientId);
        ClientRequestCreator.setNormalField(mockRequest, request);
        mockRequest.param("isBasicImage", String.valueOf(true));
        mvc.perform(mockRequest).andExpect(MockMvcResultMatchers.status().isOk());
        verify(clientService, times(1)).updateClientWithBasicImage(clientId, request);
    }
}
