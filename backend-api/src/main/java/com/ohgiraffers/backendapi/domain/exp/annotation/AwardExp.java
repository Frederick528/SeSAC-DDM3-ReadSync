package com.ohgiraffers.backendapi.domain.exp.annotation;

import com.ohgiraffers.backendapi.domain.exp.enums.ActivityType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AwardExp {
    ActivityType type(); // 어떤 활동인지 지정
}
