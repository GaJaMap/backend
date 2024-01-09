package com.map.gaja.user.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class OAuthConstant {
    public static final String KAKAO_OAUTH2_URL = "https://kapi.kakao.com/v2/user/me";
    public static final String KAKAO_ACCESS_TOKEN_HEADER = "Authorization";
    public static final String KAKAO_ACCESS_TOKEN_PREFIX = "Bearer ";
    public static final String KAKAO_ACCOUNT = "kakao_account";
    public static final String EMAIL = "email";
    public static final int FAIL_STATUS_INT = 410;
    public static final String FAIL_STATUS_STRING = "410";
    public static final String REDIRECT_PATH = "/";
    public static final String WEB_LOGIN = "WEB";
}
