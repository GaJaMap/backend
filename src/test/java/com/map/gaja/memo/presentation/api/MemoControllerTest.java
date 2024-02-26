package com.map.gaja.memo.presentation.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.map.gaja.common.ControllerTest;
import com.map.gaja.global.authentication.PrincipalDetails;
import com.map.gaja.memo.presentation.dto.request.MemoCreateRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemoControllerTest extends ControllerTest {
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("메모를 생성에 성공하면 201을 반환한다.")
    void create() throws Exception {
        // given
        Long userId = 1L;
        Long clientId = 1L;
        MemoCreateRequest request = new MemoCreateRequest("gajamap", "MESSAGE");

        // when, then
        mvc.perform(post("/api/memo/client/{clientId}", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(new PrincipalDetails(userId, "test@gmail.com", "FREE")))
        ).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("메모 메시지가 100자를 넘기면 400을 반환한다.")
    void memoMessage100Fail() throws Exception {
        // given
        Long userId = 1L;
        Long clientId = 1L;
        StringBuilder message = new StringBuilder();
        for (int i = 1; i <= 101; i++) {
            message.append(i);
        }
        MemoCreateRequest request = new MemoCreateRequest(message.toString(), "MESSAGE");

        // when, then
        mvc.perform(post("/api/memo/client/{clientId}", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(new PrincipalDetails(userId, "test@gmail.com", "FREE")))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("메모 타입이 15자를 넘기면 400을 반환한다.")
    void memoType15Fail() throws Exception {
        // given
        Long userId = 1L;
        Long clientId = 1L;
        StringBuilder memoType = new StringBuilder();
        for (int i = 1; i <= 16; i++) {
            memoType.append(i);
        }
        MemoCreateRequest request = new MemoCreateRequest(null, memoType.toString());

        // when, then
        mvc.perform(post("/api/memo/client/{clientId}", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(new PrincipalDetails(userId, "test@gmail.com", "FREE")))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("메모 타입이 null이면 400을 반환한다.")
    void memoTypeNullFail() throws Exception {
        // given
        Long userId = 1L;
        Long clientId = 1L;
        MemoCreateRequest request = new MemoCreateRequest(null, null);

        // when, then
        mvc.perform(post("/api/memo/client/{clientId}", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(new PrincipalDetails(userId, "test@gmail.com", "FREE")))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("메모를 삭제하면 200을 반환한다.")
    void delete() throws Exception {
        // given
        Long userId = 1L;
        Long memoId = 1L;

        // when, then
        mvc.perform(MockMvcRequestBuilders.delete("/api/memo/{memoId}", memoId)
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(new PrincipalDetails(userId, "test@gmail.com", "FREE")))
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("메모를 조회하면 200반환한다.")
    void read() throws Exception {
        // given
        Long userId = 1L;
        Long clientId = 1L;

        // when, then
        mvc.perform(MockMvcRequestBuilders.get("/api/memo/client/{clientId}?page=0", clientId)
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(new PrincipalDetails(userId, "test@gmail.com", "FREE")))
        ).andExpect(status().isOk());
    }
}