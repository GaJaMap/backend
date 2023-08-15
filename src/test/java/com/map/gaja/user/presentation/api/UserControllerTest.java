package com.map.gaja.user.presentation.api;

import com.map.gaja.client.apllication.ClientQueryService;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.global.authentication.PrincipalDetails;
import com.map.gaja.group.application.GroupService;
import com.map.gaja.group.presentation.dto.response.GroupInfo;
import com.map.gaja.user.application.UserService;
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

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
class UserControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    UserService userService;

    @MockBean
    GroupService groupService;

    @MockBean
    ClientQueryService clientQueryService;

    @Test
    @DisplayName("사용자가 최근에 참조한 전체 그룹 조회")
    void findWholeGroup() throws Exception {
        String email = "test@gmail.com";
        GroupInfo groupInfo = null;
        when(groupService.findGroup(email)).thenReturn(groupInfo);

        ClientListResponse clientListResponse = new ClientListResponse();
        when(clientQueryService.findAllClient(email, null)).thenReturn(clientListResponse);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/api/user/auto-login")
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(new PrincipalDetails("test@gmail.com", "FREE")));

        mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn();

        verify(clientQueryService, times(1)).findAllClient(email, null);
    }

}