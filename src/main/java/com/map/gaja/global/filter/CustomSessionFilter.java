package com.map.gaja.global.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;


@Component
public class CustomSessionFilter extends OncePerRequestFilter {
//    @Autowired
//    private SessionRepository<? extends Session> sessionRepository;

    private final Set<String> excludedPaths = new HashSet<>(
            Arrays.asList("/",
                    "/login",
                    "/api/user/login",
                    "/css",
                    "/js",
                    "/image",
                    "/file",
                    "/login/oauth2/code/kakao",
                    "/oauth2/authorization/kakao",
                    "/swagger",
                    "/swagger-ui/index.html",
                    "/swagger-ui/swagger-ui.css",
                    "/swagger-ui/index.css",
                    "/swagger-ui/swagger-ui-bundle.js",
                    "/swagger-ui/swagger-ui-standalone-preset.js",
                    "/swagger-ui/swagger-initializer.js",
                    "/v3/api-docs/swagger-config",
                    "/swagger-ui/favicon-32x32.png",
                    "/v3/api-docs/user-api",
                    "/v3/api-docs/group-api",
                    "/v3/api-docs/client-api",
                    "/favicon.ico")
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (isAnonymousUser(request)) { // 사용자한테 세션이 존재하지 않다면 익명의 세션을 생성해 줄 필요 없음.
            request.setAttribute("org.springframework.session.web.http.SessionRepositoryFilter.FILTERED", Boolean.TRUE);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAnonymousUser(HttpServletRequest request) {
        String sessionId = getSessionId(request);

        if (sessionId == null) {
            return true;
        }
        return false;
    }

    private String getSessionId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String sessionId = null;
        if (cookies != null) {
            sessionId = Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals("SESSION"))
                    .map(s -> s.getValue())
                    .findFirst()
                    .orElse(null);

            // 위 코드의 단점은 악의적인 사용자가 쿠키에 SESSION 이라는 임의로 값을 넣고 서버 요청보내면 DB에 익명 세션이 생성됨
            // 그래서 아래 코드에서 DB에 Session이 진짜로 있는지 조회해서 확실히 막을 수 있지만
            // 이 다음 필터가 SessionRepositoryFilter라서 거기서도 세션을 조회하고 있음. 그래서 DB 접근을 줄이고자 위의 코드로 일단 막아 놓음.
            // 결론은 나중에 세션 관리를 DB가 아닌 redis로 바꾸면 조회 성능도 높이고 자료도 많아서 쉽게 해결할 수 있을듯.

            // byte[] decodedCookieBytes = Base64.getDecoder().decode(sessionId);
            // String id = new String(decodedCookieBytes);
            // Session session = sessionRepository.findById(id);
        }

        return sessionId;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestUri = request.getRequestURI();

        if (excludedPaths.contains(requestUri)) {
            return true; // "/login", "/api/user/login", "/", oauth2와 swagger 관련 경로는 제외되는 경로이므로 필터링을 수행하지 않음
        }

        int secondSlashIndex = requestUri.indexOf("/", 1);
        if (secondSlashIndex == -1) { //StringIndexOutOfBoundsException 예외 처리
            return true;
        }

        requestUri = requestUri.substring(0, secondSlashIndex);
        if (excludedPaths.contains(requestUri)) {
            return true; // "/image", "/js", "/css", "/fil" "는 제외되는 경로이므로 필터링을 수행하지 않음
        }
        return false;
    }
}