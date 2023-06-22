package com.map.gaja.user.infrastructure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Oauth2ClientTest {

    @Test
    void oauth2Login() {
        Oauth2Client oauth2Client = new Oauth2Client();
        String accessToken = "accessToken";

        String email = oauth2Client.getEmail(accessToken);

        assertEquals(email, "email");
    }
}