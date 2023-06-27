package com.map.gaja.global.authentication;

import lombok.RequiredArgsConstructor;
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
    private static final int LIMIT_SESSION_SIZE = 2;
    private static final int OLD_SESSION_INDEX = 1;

    @Transactional
    public void deduplicate(String email) {
        //해당 이메일에 속한 세션 모두 가져오기
        Map<String, ? extends Session> sessions = findByIndexNameSessionRepository.findByIndexNameAndIndexValue(JdbcIndexedSessionRepository.PRINCIPAL_NAME_INDEX_NAME, email);

        if (sessions.size() == LIMIT_SESSION_SIZE) {
            List<? extends Session> sessionList = new ArrayList<>(sessions.values());
            sessionRepository.deleteById(sessionList.get(OLD_SESSION_INDEX).getId()); //가장 오래된 세션 삭제
        }
    }
}
