package com.map.gaja.user.infrastructure;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
public class Oauth2Client {
    private static final String KAKAO_OAUTH2_URL = "https://kapi.kakao.com/v2/user/me";
    private static final String KAKAO_ACCESS_TOKEN_HEADER = "Authorization";
    private static final String KAKAO_ACCESS_TOKEN_PREFIX = "Bearer ";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public String getEmail(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(KAKAO_ACCESS_TOKEN_HEADER, KAKAO_ACCESS_TOKEN_PREFIX + accessToken);

        RequestEntity<Void> request = new RequestEntity<>(headers, HttpMethod.GET, URI.create(KAKAO_OAUTH2_URL));
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                JsonNode jsonNode = mapper.readTree(response.getBody());
                return jsonNode.get("kakao_account").get("email").asText();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }
}
