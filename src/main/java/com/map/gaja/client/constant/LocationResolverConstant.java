package com.map.gaja.client.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationResolverConstant {
    public static final String AUTHORIZATION = "Authorization";
    public static final String KAKAO_AK = "KakaoAK ";
    public static final String KAKAO_URL = "https://dapi.kakao.com/v2/local/search/address.json";
    public static final String QUERY_ADDRESS_PARAM = "query";
    public static final String ANALYZE_TYPE_PARAM = "analyze_type";
    public static final String ANALYZE_TYPE_VALUE = "exact";
    public static final String RESPONSE_SIZE_PARAM = "size";
    public static final int RESPONSE_SIZE_VALUE = 1;
    public static final String DOCUMENTS_NODE_NAME = "documents";
    public static final String X_NODE_NAME = "x";
    public static final String Y_NODE_NAME = "y";
    public static final int ASYNC_CONCURRENCY = 2;
    public static final int ZERO = 0;
    public static final double MIN_LATITUDE = 33d;
    public static final double MAX_LATITUDE = 39d;
    public static final double MIN_LONGITUDE = 124d;
    public static final double MAX_LONGITUDE = 132d;
}
