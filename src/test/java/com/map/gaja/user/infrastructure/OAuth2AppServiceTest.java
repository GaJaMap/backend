package com.map.gaja.user.infrastructure;

import com.map.gaja.user.infrastructure.oauth2.OAuth2AppService;

import static org.junit.jupiter.api.Assertions.*;

class OAuth2AppServiceTest {

//    @Test
    void oauth2Login() {
        OAuth2AppService oauth2AppService = new OAuth2AppService();
        String accessToken = "accessToken";

        String email = oauth2AppService.getEmail(accessToken);

        assertEquals(email, "email");
    }
}