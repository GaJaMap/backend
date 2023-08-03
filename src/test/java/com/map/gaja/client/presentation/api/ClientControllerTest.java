package com.map.gaja.client.presentation.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.map.gaja.client.apllication.ClientAccessVerifyService;
import com.map.gaja.client.apllication.ClientQueryService;
import com.map.gaja.client.apllication.ClientService;
import com.map.gaja.client.infrastructure.s3.S3FileService;
import com.map.gaja.client.presentation.dto.request.ClientIdsRequest;
import com.map.gaja.global.authentication.PrincipalDetails;
import com.map.gaja.global.exception.ValidationErrorInfo;
import com.map.gaja.group.application.GroupAccessVerifyService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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


import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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
    @DisplayName("다수 Client 제거 성공")
    void deleteBulkClientTest() throws Exception {
        String testUrl = "/api/group/{groupId}/clients/bulk-delete";
        List<Long> clientIds = List.of(1L, 2L, 3L);
        ObjectMapper om = new ObjectMapper();
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
    @DisplayName("단일 클라이언트로 bulk delete 시도")
    void deleteBulkClientTestFail() throws Exception {
        String testUrl = "/api/group/{groupId}/clients/bulk-delete";
        List<Long> clientIds = List.of(1L);
        ObjectMapper om = new ObjectMapper();
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

    @Test
    void updateClient() {
    }

    @Test
    void addSimpleBulkClient() {
    }

    @Test
    void addClient() {
    }
}