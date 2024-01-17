package com.map.gaja.memo.domain.model;

import com.map.gaja.memo.domain.exception.MemoTypeNotFoundException;

import java.util.Arrays;

public enum MemoType {
    CALL, NAVIGATION, MESSAGE;

    public static MemoType from(String type) {
        return Arrays.stream(values())
                .filter(memo -> memo.name().equals(type))
                .findFirst()
                .orElseThrow(MemoTypeNotFoundException::new);
    }
}
