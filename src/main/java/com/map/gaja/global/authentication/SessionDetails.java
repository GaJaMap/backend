package com.map.gaja.global.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SessionDetails implements Serializable {
    private static final long serialVersionUID = 8850489178248613501L; //역.직렬화 하기 위한 버전 체크
    private Long userId;
    private String platformType; //로그인을 어디서 했는지 플랫폼(WEB or APP) 타입

}