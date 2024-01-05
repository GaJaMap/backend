package com.map.gaja.user.infrastructure.oauth2;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.map.gaja.user.domain.exception.UserNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static com.map.gaja.user.constant.OAuthConstant.*;

@Component
public class KakaoEmailProvider {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public String getEmail(String accessToken) {
        ResponseEntity<String> response = requestKakaoLogin(accessToken);

        String email = extractEmail(response);

        return email;
    }

    private String extractEmail(ResponseEntity<String> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new UserNotFoundException();
        }

        String email = null;
        try {
            JsonNode jsonNode = mapper.readTree(response.getBody());
            email = jsonNode.get(KAKAO_ACCOUNT).get(EMAIL).asText();
        } catch (JsonProcessingException e) {
            throw new UserNotFoundException(e);
        }

        if (email == null) {
            throw new UserNotFoundException();
        }

        return email;
    }

    private ResponseEntity<String> requestKakaoLogin(String accessToken) {
        HttpHeaders headers = createHttpHeaders(accessToken);

        RequestEntity<Void> request = new RequestEntity<>(headers, HttpMethod.GET, URI.create(KAKAO_OAUTH2_URL));

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);
        return response;
    }

    private HttpHeaders createHttpHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(KAKAO_ACCESS_TOKEN_HEADER, KAKAO_ACCESS_TOKEN_PREFIX + accessToken);
        return headers;
    }
}
