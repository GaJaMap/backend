package com.map.gaja.client.presentation.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.map.gaja.client.application.*;
import com.map.gaja.client.domain.exception.InvalidFileException;
import com.map.gaja.client.infrastructure.s3.S3FileService;
import com.map.gaja.client.application.validator.ClientRequestValidator;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.request.simple.SimpleClientBulkRequest;
import com.map.gaja.client.presentation.dto.request.simple.SimpleNewClientRequest;
import com.map.gaja.client.presentation.dto.response.ClientOverviewResponse;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import com.map.gaja.global.authentication.AuthenticationRepository;
import com.map.gaja.global.authentication.PrincipalDetails;
import com.map.gaja.group.application.GroupAccessVerifyService;
import com.map.gaja.group.domain.exception.GroupNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
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

@WebMvcTest(ClientSavingController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class ClientSavingControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    ClientBulkService clientBulkService;
    @MockBean
    GroupAccessVerifyService groupAccessVerifyService;
    @MockBean
    ClientRequestValidator clientRequestValidator;
    @MockBean
    AuthenticationRepository authenticationRepository;
    @MockBean
    ClientSavingService clientSavingService;

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

        ClientOverviewResponse response = new ClientOverviewResponse();
        StoredFileDto fileDto = new StoredFileDto("test", "test");
        response.setImage(fileDto);
        when(clientSavingService.saveClientWithImage(any(),any())).thenReturn(response);

        mvc.perform(mockRequest).andExpect(MockMvcResultMatchers.status().isCreated());
        verify(clientSavingService, times(1)).saveClientWithImage(any(), any());
    }

    @Test
    @DisplayName("이미지와 함께 고객 저장 중 DB 오류 발생")
    void addClientWithImageExceptionTest() throws Exception {
        String testUrl = "/api/clients";
        NewClientRequest request = ClientRequestCreator.createValidNewRequest(groupId);
        MockHttpServletRequestBuilder mockRequest = ClientRequestCreator.createPostRequestWithImage(testUrl);
        ClientRequestCreator.setNormalField(mockRequest, request);
        mockRequest.param("isBasicImage", String.valueOf(false));
        when(clientSavingService.saveClientWithImage(any(), any())).thenThrow(new GroupNotFoundException());

        mvc.perform(mockRequest).andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
        verify(clientSavingService, times(1)).saveClientWithImage(any(), any());
    }

    @Test
    @DisplayName("다수 고객 등록")
    void addSimpleBulkClient() throws Exception {
        String testUrl = "/api/clients/bulk";
        List<SimpleNewClientRequest> clientsRequest = List.of(
                new SimpleNewClientRequest("aaa", "01011112222"),
                new SimpleNewClientRequest("aaa", "01011112222")
        );

        SimpleClientBulkRequest request = new SimpleClientBulkRequest(groupId, clientsRequest);
        String jsonBody = om.writeValueAsString(request);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(testUrl, groupId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .with(SecurityMockMvcRequestPostProcessors.user(new PrincipalDetails(1L, "test@gmail.com", "FREE")));

        mvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated());
        verify(clientBulkService, times(1)).saveSimpleClientList(request, "test@gmail.com");
    }

}
