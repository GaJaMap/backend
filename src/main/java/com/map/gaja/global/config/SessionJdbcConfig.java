package com.map.gaja.global.config;

import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

@EnableJdbcHttpSession(tableName = "session", maxInactiveIntervalInSeconds = 3600)
public class SessionJdbcConfig {

}
