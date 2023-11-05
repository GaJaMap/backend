package com.map.gaja.global.authentication.imageuploads;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 이미지 업로드 기능은 일반(FREE) 사용자는 사용할 수 없다.
 * 이미지 업로드 기능이 있는 컨트롤러에는 해당 어노테이션을 메소드에 붙혀서 AOP를 통해 막을 수 있다.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ImageAuthChecking {
}
