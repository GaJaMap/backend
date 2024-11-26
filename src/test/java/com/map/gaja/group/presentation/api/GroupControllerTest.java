package com.map.gaja.group.presentation.api;

import com.map.gaja.common.ControllerTest;
import com.map.gaja.global.authentication.PrincipalDetails;
import com.map.gaja.group.presentation.dto.request.GroupCreateRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.Charset;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GroupControllerTest extends ControllerTest {

    @Test
    @DisplayName("@Valid 테스트")
    @WithMockUser
    void validTest() throws Exception {
        //given
        GroupCreateRequest request = new GroupCreateRequest(null);
        String requestToString = mapper.writeValueAsString(request);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/api/group")
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(new PrincipalDetails(1L, "test@gmail.com", "FREE")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestToString);

        MvcResult result = mvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String message = result.getResponse().getContentAsString(Charset.forName("UTF-8"));
        System.out.println(message); //BindException에서 List형태의 message로 반환됨

    }
}