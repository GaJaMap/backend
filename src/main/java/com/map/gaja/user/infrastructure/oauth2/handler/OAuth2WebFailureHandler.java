package com.map.gaja.user.infrastructure.oauth2.handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.map.gaja.user.constant.OAuthConstant.FAIL_STATUS_INT;

@Component
public class OAuth2WebFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        HttpSession session = request.getSession();
        if (isAnonymousSession(session)) {
            session.invalidate();
        }

        response.setStatus(FAIL_STATUS_INT); //회원 탈퇴한 유저가 로그인할 경우 예외 응답
    }

    private boolean isAnonymousSession(HttpSession session) {
        return session != null;
    }
}
