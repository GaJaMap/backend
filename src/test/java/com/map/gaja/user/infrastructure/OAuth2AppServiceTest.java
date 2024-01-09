package com.map.gaja.user.infrastructure;

import com.map.gaja.user.infrastructure.oauth2.KakaoEmailProvider;

import static org.junit.jupiter.api.Assertions.*;

class OAuth2AppServiceTest {

//    @Test
    void oauth2Login() {
        KakaoEmailProvider oauth2AppService = new KakaoEmailProvider();
        String accessToken = "accessToken";

        String email = oauth2AppService.getEmail(accessToken);

        assertEquals(email, "email");
    }
}