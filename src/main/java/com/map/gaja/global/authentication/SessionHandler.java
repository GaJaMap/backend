package com.map.gaja.global.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SessionHandler {
    private final FindByIndexNameSessionRepository findByIndexNameSessionRepository;
    private final SessionRepository sessionRepository;

    /**
     * 앱 or 웹 플랫폼에 중복된 세션이 있으면 제거
     */
    @Transactional
    public void deduplicate(String email, String platformType) {
        Map<String, ? extends Session> sessions = getSessions(email); //해당 이메일에 속한 세션 모두 가져오기

        sessions.values().stream()
                .filter(session -> isDuplicate(platformType, extractPrincipalDetails(session).getPlatformType())) // 같은 플랫폼(웹 or 앱)에서 로그인을 한 적이 있다면 중복로그인
                .forEach(session -> sessionRepository.deleteById(session.getId())); //중복 로그인으로 이전 세션 제거
    }

    /**
     * 회원 탈퇴시 웹과 앱 세션 삭제
     */
    public void deleteAllByEmail(String email) {
        Map<String, ? extends Session> sessions = getSessions(email);

        sessions.values().stream()
                .forEach(session -> sessionRepository.deleteById(session.getId()));
    }

    private Map<String, ? extends Session> getSessions(String email) {
        return findByIndexNameSessionRepository.findByPrincipalName(email);
    }


    private boolean isDuplicate(String platformType, String platformType1) {
        if (platformType.equals(platformType1)) {
            return true;
        }
        return false;
    }

    private PrincipalDetails extractPrincipalDetails(Session session) {
        Object object = session.getAttribute("SPRING_SECURITY_CONTEXT");
        SecurityContextImpl securityContext = (SecurityContextImpl) object;
        Authentication authentication = securityContext.getAuthentication();
        return (PrincipalDetails) authentication.getPrincipal();
    }
}