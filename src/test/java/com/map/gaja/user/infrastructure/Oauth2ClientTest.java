package com.map.gaja.user.infrastructure;

import com.map.gaja.user.infrastructure.oauth2.OAuth2Appservice;

import static org.junit.jupiter.api.Assertions.*;

class Oauth2ClientTest {

//    @Test
    void oauth2Login() {
        OAuth2Appservice oauth2AppService = new OAuth2Appservice();
        String accessToken = "accessToken";

        String email = oauth2AppService.getEmail(accessToken);

        assertEquals(email, "email");
    }
}