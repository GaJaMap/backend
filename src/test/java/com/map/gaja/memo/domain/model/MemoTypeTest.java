package com.map.gaja.memo.domain.model;

import com.map.gaja.memo.domain.exception.MemoTypeNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemoTypeTest {
    @Test
    @DisplayName("MemoType CALL을 가져온다.")
    void getCallMemoType() {
        assertThat(MemoType.CALL)
                .isEqualTo(MemoType.from("CALL"));
    }

    @Test
    @DisplayName("MemoType MESSAGE을 가져온다.")
    void getMessageMemoType() {
        assertThat(MemoType.MESSAGE)
                .isEqualTo(MemoType.from("MESSAGE"));
    }

    @Test
    @DisplayName("MemoType NAVIGATION을 가져온다.")
    void getNavigationMemoType() {
        assertThat(MemoType.NAVIGATION)
                .isEqualTo(MemoType.from("NAVIGATION"));
    }

    @Test
    @DisplayName("MemoType을 가져오는데 실패한다")
    void getMemoTypeFail() {
        assertThatThrownBy(() -> MemoType.from("WOWOW"))
                .isInstanceOf(MemoTypeNotFoundException.class);
    }


}