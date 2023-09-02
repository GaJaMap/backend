package com.map.gaja.global.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SessionHandler {
    private final FindByIndexNameSessionRepository findByIndexNameSessionRepository;
    private final SessionRepository sessionRepository;

    @Transactional
    public void deduplicate(String email, String platformType) {
        //해당 이메일에 속한 세션 모두 가져오기
        Map<String, ? extends Session> sessions = findByIndexNameSessionRepository.findByIndexNameAndIndexValue(JdbcIndexedSessionRepository.PRINCIPAL_NAME_INDEX_NAME, email);

        if (sessions.isEmpty()) {
            return;
        }

        List<? extends Session> sessionList = new ArrayList<>(sessions.values());
        for (Session session : sessionList) {
            PrincipalDetails principalDetails = extractPrincipalDetails(session);

            // 같은 플랫폼(웹 or 앱)에서 로그인을 한 적이 있다면 중복로그인
            if (isDuplicate(platformType, principalDetails.getPlatformType())) {
                sessionRepository.deleteById(session.getId()); //중복 로그인으로 이전 세션 제거
            }
        }
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